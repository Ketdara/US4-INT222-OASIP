package sit.int221.us4backend.services;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.entities.Event;
import sit.int221.us4backend.entities.EventCategoryOwner;
import sit.int221.us4backend.repositories.EventCategoryOwnerRepository;
import sit.int221.us4backend.repositories.EventRepository;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.utils.DateTimeManager;
import sit.int221.us4backend.utils.EventValidator;
import sit.int221.us4backend.utils.JwtTokenUtil;
import sit.int221.us4backend.utils.ListMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    private Sort sort = Sort.by("eventStartTime").descending();
    private Integer maxDuration = 8;    // hours

    private Integer beforeEventBuffer = maxDuration * -1;
    private Integer afterEventBuffer = maxDuration;

    // Date buffer (starts from midnight ICT)
    private Integer beforeDateBuffer = maxDuration * -1;
    private Integer afterDateBuffer = 24 + maxDuration;

    public Page<EventPartialDTO> getEventDTOsAsPage(Integer page, Integer pageSize, Claims claims) {
        Page<Event> eventPage;

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(roles.contains("admin")) eventPage = eventRepository.findAll(PageRequest.of(page, pageSize, sort));
        else if(roles.contains("student")) eventPage = eventRepository.findAllByBookingEmail(email, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("lecturer")) eventPage = eventRepository.findAllByLecturerEmail(email, PageRequest.of(page, pageSize));
        else if(roles.isEmpty() == true) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByCategoryAsPage(Integer page, Integer pageSize, Integer categoryId, Claims claims) {
        if(categoryId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID not specified");
        Page<Event> eventPage;

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(roles.contains("admin")) eventPage = eventRepository.findAllByEventCategory_Id(categoryId, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("student")) eventPage = eventRepository.findAllByEventCategory_IdAndBookingEmail(categoryId, email, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("lecturer")) eventPage = eventRepository.findAllByEventCategory_IdAndLecturerEmail(email, categoryId, PageRequest.of(page, pageSize));
        else if(roles.isEmpty() == true) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByUpcomingAsPage(Integer page, Integer pageSize, String nowISOString, Claims claims) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);
        Page<Event> eventPage;

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(roles.contains("admin")) eventPage = eventRepository.findEventUpcomingAll(now, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("student")) eventPage = eventRepository.findEventUpcomingAllAndEmail(now, email, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("lecturer")) eventPage = eventRepository.findEventUpcomingAllAndLecturerEmail(now, email, PageRequest.of(page, pageSize));
        else if(roles.isEmpty() == true) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByPastAsPage(Integer page, Integer pageSize, String nowISOString, Claims claims) {
        if(nowISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp not specified");
        String now = dateTimeManager.ISOStringToDateString(nowISOString);
        Page<Event> eventPage;

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(roles.contains("admin")) eventPage = eventRepository.findEventPastAll(now, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("student")) eventPage = eventRepository.findEventPastAllAndEmail(now, email, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("lecturer")) eventPage = eventRepository.findEventPastAllAndLecturerEmail(now, email, PageRequest.of(page, pageSize));
        else if(roles.isEmpty() == true) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your role is invalid");

        return mapPageAndFormatStartTime(eventPage);
    }

    public Page<EventPartialDTO> getEventDTOsByDateAsPage(Integer page, Integer pageSize, String dateISOString, Claims claims) {
        if(dateISOString == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ICT Date timestamp not specified");
        String thisDate = dateTimeManager.ISOStringToDateString(dateISOString);
        String nextDate = dateTimeManager.ISOStringToOffsetDateString(dateISOString, 24); // next 24 hours
        Page<Event> eventPage;

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(roles.contains("admin")) eventPage = eventRepository.findAllByEventStartTimeBetween(thisDate, nextDate, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("student")) eventPage = eventRepository.findAllByEventStartTimeBetweenAndBookingEmail(thisDate, nextDate, email, PageRequest.of(page, pageSize, sort));
        else if(roles.contains("lecturer")) eventPage = eventRepository.findAllByEventStartTimeBetweenAndLecturerEmail(thisDate, nextDate, email, PageRequest.of(page, pageSize));
        else if(roles.isEmpty() == true) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
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

    public EventWithValidateDTO getEventDTOById(Integer event_id, Claims claims) {
        Event event = eventRepository.findById(event_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + event_id + " not found or does not exist"));

        Integer userId = jwtTokenUtil.getUserIdAsInt(claims);
        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(!roles.contains("admin")) {      // not admin
            if(!roles.contains("student")) {      // not student
                if(!roles.contains("lecturer")) {       // not lecturer
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
                }else {     // lecturer
                    userId = userRepository.findByEmail(email).getId();
                    List<EventCategoryOwner> lecturerCategory = eventCategoryOwnerRepository.findAllById_UserId(userId);
                    if(!lecturerCategory.stream().anyMatch(category -> category.getId().getEventCategoryId() == event.getEventCategory().getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
                    }
                }
            }else if(!event.getBookingEmail().equals(email)) {
                if(!roles.contains("lecturer")) {       // invalid student
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not match with current user email");
                }else {     // lecturer
                    List<EventCategoryOwner> lecturerCategory = eventCategoryOwnerRepository.findAllById_UserId(userId);
                    if(!lecturerCategory.stream().anyMatch(category -> category.getId().getEventCategoryId() == event.getEventCategory().getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this event");
                    }
                }
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

    public Event postEventDTO(EventWithValidateDTO newEventDTO, Claims claims, MultipartFile file) {
        callEventValidator(newEventDTO, false);

        String email = null;
        ArrayList roles = new ArrayList();
        if(claims != null) {
            email = jwtTokenUtil.getEmailAsString(claims);
            roles = jwtTokenUtil.getRolesAsArrayList(claims);
        }

        if(!roles.contains("admin") && !roles.isEmpty()) {
            if(!roles.contains("student")) {        // lecturer
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot access this feature");
            }
            else if(!newEventDTO.getBookingEmail().equals(email)) {     // invalid student
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not match with current user email");
            }
        }

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

    public Event putEventDTO(EventWithValidateDTO newEventDTO, Integer event_id, Claims claims, boolean isFileUpdate, MultipartFile file) {
        callEventValidator(newEventDTO, true);

        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(!roles.contains("admin")) {
            if(!roles.contains("student") || !newEventDTO.getBookingEmail().equals(email)) {    // lecturer or invalid email
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot edit this event");
            }
        }


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

    public void deleteEventDTOById(Integer event_id, Claims claims) {
        EventWithValidateDTO event = getEventDTOByIdShort(event_id);

        String email = jwtTokenUtil.getEmailAsString(claims);
        ArrayList roles = jwtTokenUtil.getRolesAsArrayList(claims);

        if(!roles.contains("admin")) {
            if(!roles.contains("student") || !event.getBookingEmail().equals(email)) {    // lecturer or invalid email
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot edit this event");
            }
        }


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
