package sit.int221.us4backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.entities.Event;
import sit.int221.us4backend.entities.EventCategoryOwner;
import sit.int221.us4backend.entities.User;
import sit.int221.us4backend.repositories.EventCategoryOwnerRepository;
import sit.int221.us4backend.repositories.EventRepository;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.utils.DateTimeManager;
import sit.int221.us4backend.utils.EventValidator;
import sit.int221.us4backend.utils.ListMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    private Sort sort = Sort.by("eventStartTime").descending();
    private Integer maxDuration = 8;    // hours

    private Integer beforeEventBuffer = maxDuration * -1;
    private Integer afterEventBuffer = maxDuration;

    // Date buffer (starts from midnight ICT)
    private Integer beforeDateBuffer = maxDuration * -1;
    private Integer afterDateBuffer = 24 + maxDuration;

    public Page<EventPartialDTO> getEventDTOsAsPage(Integer page, Integer pageSize, User user) {
        Page<Event> eventPage;
        String userRole = user.getRole();

        if(userRole.equals("admin")) eventPage = eventRepository.findAll(PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("student")) eventPage = eventRepository.findAllByBookingEmail(user.getEmail(), PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("lecturer")) eventPage = eventRepository.findAllByLecturerId(user.getId(), PageRequest.of(page, pageSize));
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByCategoryAsPage(Integer page, Integer pageSize, Integer categoryId, User user) {
        if(categoryId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID not specified");
        Page<Event> eventPage;
        String userRole = user.getRole();

        if(userRole.equals("admin")) eventPage = eventRepository.findAllByEventCategory_Id(categoryId, PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("student")) eventPage = eventRepository.findAllByEventCategory_IdAndBookingEmail(categoryId, user.getEmail(), PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("lecturer")) eventPage = eventRepository.findAllByEventCategory_IdAndLecturerId(user.getId(), categoryId, PageRequest.of(page, pageSize));
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByUpcomingAsPage(Integer page, Integer pageSize, String nowISOString, User user) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);
        Page<Event> eventPage;
        String userRole = user.getRole();

        if(userRole.equals("admin")) eventPage = eventRepository.findEventUpcomingAll(now, PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("student")) eventPage = eventRepository.findEventUpcomingAllAndEmail(now, user.getEmail(), PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("lecturer")) eventPage = eventRepository.findEventUpcomingAllAndLecturerId(now, user.getId(), PageRequest.of(page, pageSize));
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByPastAsPage(Integer page, Integer pageSize, String nowISOString, User user) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);
        Page<Event> eventPage;
        String userRole = user.getRole();

        if(userRole.equals("admin")) eventPage = eventRepository.findEventPastAll(now, PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("student")) eventPage = eventRepository.findEventPastAllAndEmail(now, user.getEmail(), PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("lecturer")) eventPage = eventRepository.findEventPastAllAndLecturerId(now, user.getId(), PageRequest.of(page, pageSize));
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByDateAsPage(Integer page, Integer pageSize, String dateISOString, User user) {
        if(dateISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ICT Date timestamp not specified");
        String thisDate = dateTimeManager.ISOStringToDateString(dateISOString);
        String nextDate = dateTimeManager.ISOStringToOffsetDateString(dateISOString, 24); // next 24 hours
        Page<Event> eventPage;
        String userRole = user.getRole();

        if(userRole.equals("admin")) eventPage = eventRepository.findAllByEventStartTimeBetween(thisDate, nextDate, PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("student")) eventPage = eventRepository.findAllByEventStartTimeBetweenAndBookingEmail(thisDate, nextDate, user.getEmail(), PageRequest.of(page, pageSize, sort));
        else if(userRole.equals("lecturer")) eventPage = eventRepository.findAllByEventStartTimeBetweenAndLecturerId(thisDate, nextDate, user.getId(), PageRequest.of(page, pageSize));
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

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

    public EventWithValidateDTO getEventDTOById(Integer event_id, User user) {
        Event event = eventRepository.findById(event_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + event_id + " not found or does not exist"));
        event.setEventStartTime(dateTimeManager.dateStringToISOString(event.getEventStartTime()));
        String userRole = user.getRole();

        if(userRole.equals("student") && !event.getBookingEmail().equals(user.getEmail())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
        if(userRole.equals("lecturer")) {
            List<EventCategoryOwner> lecturerCategory = eventCategoryOwnerRepository.findAllById_UserId(user.getId());
            if(!lecturerCategory.stream().anyMatch(category -> category.getId().getEventCategoryId() == event.getEventCategory().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
            }
        }
        EventWithValidateDTO selectedEvent = modelMapper.map(event, EventWithValidateDTO.class);
        if(selectedEvent.getFileName() != null) {
            selectedEvent.setFile(getFile(selectedEvent.getFileName(), event_id));
        }
        return selectedEvent;
    }

    public EventWithValidateDTO getEventDTOByIdShort(Integer event_id) {
        Event event = eventRepository.findById(event_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + event_id + " not found or does not exist"));
        event.setEventStartTime(dateTimeManager.dateStringToISOString(event.getEventStartTime()));
        return modelMapper.map(event, EventWithValidateDTO.class);
    }

    public Event postEventDTO(EventWithValidateDTO newEventDTO, User user, MultipartFile file) {
        callEventValidator(newEventDTO, false);
        String userRole = user.getRole();

        if(userRole.equals("student") && !newEventDTO.getBookingEmail().equals(user.getEmail())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not match with current user email");
        if(userRole.equals("lecturer")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this feature");

        newEventDTO.setEventStartTime(dateTimeManager.ISOStringToDateString(newEventDTO.getEventStartTime()));
        trimEventField(newEventDTO);
        Event newEvent = modelMapper.map(newEventDTO, Event.class);
        // generate id
        Event createdEvent = eventRepository.saveAndFlush(newEvent);

        // save file + save id_filename as filename
        if(file != null){
            postFile(file, createdEvent.getId());
            newEvent.setFileName(file.getOriginalFilename());

            // update filename in DB
            Event event = eventRepository.findById(createdEvent.getId()).map(oldEvent -> mapEvent(oldEvent, newEvent))
                    .orElseGet(() ->
                    {
                        newEvent.setId(createdEvent.getId());
                        return newEvent;
                    });

            return eventRepository.saveAndFlush(event);
        }else return createdEvent;
    }

    public Event putEventDTO(EventWithValidateDTO newEventDTO, Integer event_id, User user, boolean isFileUpdate, MultipartFile file) {
        callEventValidator(newEventDTO, true);
        String userRole = user.getRole();

        if(userRole.equals("student") && !newEventDTO.getBookingEmail().equals(user.getEmail())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot edit this event");
        if(userRole.equals("lecturer")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this feature");

        newEventDTO.setEventStartTime(dateTimeManager.ISOStringToDateString(newEventDTO.getEventStartTime()));
        trimEventField(newEventDTO);

        EventWithValidateDTO oldEventDTO = getEventDTOByIdShort(newEventDTO.getId());
        if(isFileUpdate == true) {
            if(oldEventDTO.getFileName() != null) {
                deleteFile(oldEventDTO.getFileName(), event_id);
            }

            if(file != null){
                postFile(file, event_id);
                newEventDTO.setFileName(file.getOriginalFilename());
                System.out.println("1 - " + file.getOriginalFilename());
            }
        }

        Event newEvent = modelMapper.map(newEventDTO, Event.class);
        System.out.println("2 - " + newEvent.getFileName());
        Event event = eventRepository.findById(event_id).map(oldEvent -> mapEvent(oldEvent, newEvent))
                .orElseGet(() ->
                {
                    newEvent.setId(event_id);
                    return newEvent;
                });

        return  eventRepository.saveAndFlush(event);
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
        oldEvent.setFileName(newEvent.getFileName());
        return oldEvent;
    }

    public void deleteEventDTOById(Integer event_id, User user) {
        EventWithValidateDTO event = getEventDTOByIdShort(event_id);
        String userRole = user.getRole();

        if(userRole.equals("student") && !event.getBookingEmail().equals(user.getEmail())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot delete this event");
        if(userRole.equals("lecturer")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this feature");


        eventRepository.deleteById(event_id);
        if(event.getFileName() != null) {
            deleteFile(event.getFileName(), event_id);
        }
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
            EventWithValidateDTO oldEventDTO = getEventDTOByIdShort(newEventDTO.getId());
            violationStringBuilder.append(eventValidator.putFieldValidate(newEventDTO, oldEventDTO));
        }

        String violationString = violationStringBuilder.toString();
        if(violationString.length() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not valid; " + violationString);
    }

    public Page<EventPartialDTO> getEventDTOsByEmailAsPage(Integer page, Integer pageSize, String email) {
        if(email == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not specified");

        Page<Event> eventPage = eventRepository.findAllByBookingEmail(email, PageRequest.of(page, pageSize, sort));
        return mapPageAndFormatStartTime(eventPage);
    }


    @Value("${file.upload.path}")
    private String fileUploadPath;

    private String postFile(MultipartFile file, Integer event_id) {
        String pathString = null;
        try{
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fileUploadPath + event_id.toString() + "_" + file.getOriginalFilename());
            Files.write(path, bytes);
            pathString = path.toString();
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return pathString;
    }

    private byte[] getFile(String fileName, Integer event_id) {
        byte[] file = null;
        try {
            Path path = Paths.get(fileUploadPath + event_id.toString() + "_" + fileName);
            file = Files.readAllBytes(path);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return file;
    }

    private void deleteFile(String fileName, Integer event_id) {
        try {
            Path path = Paths.get(fileUploadPath + event_id.toString() + "_" + fileName);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
