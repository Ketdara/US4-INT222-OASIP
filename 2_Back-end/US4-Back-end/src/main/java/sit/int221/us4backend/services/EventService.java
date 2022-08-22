package sit.int221.us4backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.entities.Event;
import sit.int221.us4backend.repositories.EventRepository;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.utils.DateTimeManager;
import sit.int221.us4backend.utils.EventValidator;
import sit.int221.us4backend.utils.ListMapper;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private DateTimeManager dateTimeManager;
    @Autowired
    private EventValidator eventValidator;

    private Sort sort = Sort.by("eventStartTime").descending();
    private Integer maxDuration = 8;    // hours

    private Integer beforeEventBuffer = maxDuration * -1;
    private Integer afterEventBuffer = maxDuration;

    // Date buffer (starts from midnight ICT)
    private Integer beforeDateBuffer = maxDuration * -1;
    private Integer afterDateBuffer = 24 + maxDuration;

    public Page<EventPartialDTO> getEventDTOsAsPage(Integer page, Integer pageSize) {
        Page<Event> eventPage = eventRepository.findAll(PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByCategoryAsPage(Integer page, Integer pageSize, Integer categoryId) {
        if(categoryId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID not specified");

        Page<Event> eventPage = eventRepository.findAllByEventCategory_Id(categoryId, PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByUpcomingAsPage(Integer page, Integer pageSize, String nowISOString) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);

        Page<Event> eventPage = eventRepository.findEventUpcomingAll(now, PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByPastAsPage(Integer page, Integer pageSize, String nowISOString) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);

        Page<Event> eventPage = eventRepository.findEventPastAll(now, PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByDateAsPage(Integer page, Integer pageSize, String dateISOString) {
        if(dateISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ICT Date timestamp not specified");
        String thisDate = dateTimeManager.ISOStringToDateString(dateISOString);
        String nextDate = dateTimeManager.ISOStringToOffsetDateString(dateISOString, 24); // next 24 hours

        Page<Event> eventPage = eventRepository.findAllByEventStartTimeBetween(thisDate, nextDate, PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }

    private Page<EventPartialDTO> mapPageAndFormatStartTime(Page<Event> eventPage) {
        eventPage.forEach(event -> {
            event.setEventStartTime(dateTimeManager.dateStringToISOString(event.getEventStartTime()));
        });
        return eventPage.map(entity -> modelMapper.map(entity, EventPartialDTO.class));
    }

    public List<EventTimeframeDTO> getEventTimeframeDTOs(String selectedISOString, Integer categoryId, boolean isDateBuffer) {
        if(selectedISOString == null || categoryId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Overlap timeframe not specified");
        }

        Integer bufferBefore;
        Integer bufferAfter;
        if(isDateBuffer) { bufferBefore = beforeDateBuffer; bufferAfter = afterDateBuffer; }
        else { bufferBefore = beforeEventBuffer; bufferAfter = afterEventBuffer; }

        String minDate = dateTimeManager.ISOStringToOffsetDateString(selectedISOString, bufferBefore);
        String maxDate = dateTimeManager.ISOStringToOffsetDateString(selectedISOString, bufferAfter);

        List<Event> eventList = eventRepository.findAllByEventStartTimeBetweenAndEventCategory_Id(minDate, maxDate, categoryId);
        List<EventTimeframeDTO> timeframes = listMapper.mapList(eventList, EventTimeframeDTO.class, modelMapper);
        timeframes.forEach(timeframe -> {
            String startDateString = timeframe.getEventStartTime();
            Integer duration = timeframe.getEventDuration();

            timeframe.setEventStartTime(dateTimeManager.dateStringToISOString(startDateString));
            timeframe.setEventEndTime(dateTimeManager.dateStringToEndDateISOString(startDateString, duration));
        });
        return timeframes;
    }

    public EventWithValidateDTO getEventDTOById(Integer event_id) {
        Event event = eventRepository.findById(event_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + event_id + " not found or does not exist."));
        event.setEventStartTime(dateTimeManager.dateStringToISOString(event.getEventStartTime()));
        return modelMapper.map(event, EventWithValidateDTO.class);
    }

    public Event postEventDTO(EventWithValidateDTO newEventDTO) {
        callEventValidator(newEventDTO, false);

        newEventDTO.setEventStartTime(dateTimeManager.ISOStringToDateString(newEventDTO.getEventStartTime()));
        trimEventField(newEventDTO);
        Event newEvent = modelMapper.map(newEventDTO, Event.class);
        return eventRepository.saveAndFlush(newEvent);
    }

    public Event putEventDTO(EventWithValidateDTO newEventDTO, Integer event_id) {
        callEventValidator(newEventDTO, true);

        newEventDTO.setEventStartTime(dateTimeManager.ISOStringToDateString(newEventDTO.getEventStartTime()));
        trimEventField(newEventDTO);
        Event newEvent = modelMapper.map(newEventDTO, Event.class);
        Event event = eventRepository.findById(event_id).map(oldEvent -> mapEvent(oldEvent, newEvent))
                .orElseGet(() ->
                {
                    newEvent.setId(event_id);
                    return newEvent;
                });
        return eventRepository.saveAndFlush(event);
    }

    private EventWithValidateDTO trimEventField(EventWithValidateDTO newEventDTO) {
        newEventDTO.setBookingName(newEventDTO.getBookingName().trim());
        newEventDTO.setBookingEmail(newEventDTO.getBookingEmail().trim());
        if(newEventDTO.getEventNotes() != null) newEventDTO.setEventNotes(newEventDTO.getEventNotes().trim());
        return newEventDTO;
    }

    private Event mapEvent(Event oldEvent, Event newEvent) {
        oldEvent.setEventStartTime(newEvent.getEventStartTime());
        oldEvent.setEventNotes(newEvent.getEventNotes());
        return oldEvent;
    }

    public void deleteEventDTOById(Integer event_id) {
        eventRepository.findById(event_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + event_id + " not found or does not exist."));
        eventRepository.deleteById(event_id);
    }

    private void callEventValidator(EventWithValidateDTO newEventDTO, boolean isPut) {
        StringBuilder violationStringBuilder = new StringBuilder();
        violationStringBuilder.append(eventValidator.annotationValidate(newEventDTO));
        violationStringBuilder.append(eventValidator.eventCategoryNullValidate(newEventDTO));

        if(newEventDTO.getEventCategory() != null && newEventDTO.getEventStartTime() != null) {
            // get timeframes from service
            List<EventTimeframeDTO> timeframes = getEventTimeframeDTOs(newEventDTO.getEventStartTime(), newEventDTO.getEventCategory().getId(), false);
            newEventDTO.setEventDuration(newEventDTO.getEventCategory().getEventDuration());
            violationStringBuilder.append(eventValidator.overlapValidate(newEventDTO, timeframes));
        }

        if(isPut == true) {
            EventWithValidateDTO oldEventDTO = getEventDTOById(newEventDTO.getId());
            violationStringBuilder.append(eventValidator.putFieldValidate(newEventDTO, oldEventDTO));
        }

        String violationString = violationStringBuilder.toString();
        if(violationString.length() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not valid; " + violationString);
    }
}
