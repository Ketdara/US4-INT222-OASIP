package sit.int221.us4backend.controllers;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.entities.Event;
import sit.int221.us4backend.entities.User;
import sit.int221.us4backend.repositories.EventCategoryOwnerRepository;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.services.EventService;
import sit.int221.us4backend.utils.DateTimeManager;
import sit.int221.us4backend.utils.EmailUtil;
import sit.int221.us4backend.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private DateTimeManager dateTimeManager;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public Page<EventPartialDTO> getEventsAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOsAsPage(page, pageSize, user);
    }

    @GetMapping("/category")
    public Page<EventPartialDTO> getEventsAllByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer categoryId,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOsByCategoryAsPage(page, pageSize, categoryId, user);
    }

    @GetMapping("/upcoming")
    public Page<EventPartialDTO> getEventsAllByUpcoming(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOsByUpcomingAsPage(page, pageSize, now, user);
    }

    @GetMapping("/past")
    public Page<EventPartialDTO> getEventsAllByPast(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOsByPastAsPage(page, pageSize, now, user);
    }

    @GetMapping("/date")
    public Page<EventPartialDTO> getEventsAllByDate(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String date,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOsByDateAsPage(page, pageSize, date, user);
    }

    @GetMapping("/{event_id}")
    public EventWithValidateDTO getEventById(
            @PathVariable Integer event_id,
            HttpServletRequest request,
            HttpServletResponse response) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        return eventService.getEventDTOById(event_id, user);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEvent(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String event,
            HttpServletRequest request) {

//        if(file.getSize() > 10485760) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds 10 MB");
        String tokenEmail;
        User user;

        try {
            jwtTokenUtil.validateTokenFromHeader(request);
            tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
            user = getUserFromEmail(tokenEmail);
        } catch(ResponseStatusException e){
            user = new User();
            user.setRole("guest");
        }

        EventWithValidateDTO newEventDTO = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(event);
            newEventDTO = modelMapper.map(json, EventWithValidateDTO.class);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Event postedEvent = eventService.postEventDTO(newEventDTO, user, file);
        String to = postedEvent.getBookingEmail();
        String bookingName = postedEvent.getBookingName();
        String eventCategory = postedEvent.getEventCategory().getEventCategoryName();
        String when = dateTimeManager.getEmailDate(postedEvent.getEventStartTime(), postedEvent.getEventDuration());
        String eventNotes = postedEvent.getEventNotes();

        String subject = "[OASIP] " + eventCategory + " @ " + when;
        String text = "Booking Name: PBI 36 " + bookingName
                + "\nEvent Category: " + eventCategory
                + "\nWhen: " + when
                + "\nEvent Notes: " + eventNotes;
        emailUtil.sendRealEmail(to, subject, text);
    }

//    @PutMapping("/{event_id}")
//    public void putEvent(
//            @RequestBody EventWithValidateDTO newEventDTO,
//            @PathVariable Integer event_id,
//            HttpServletRequest request) {
//
//        jwtTokenUtil.validateTokenFromHeader(request);
//        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
//        User user = getUserFromEmail(tokenEmail);
//
//        eventService.putEventDTO(newEventDTO, event_id, user);
//    }

    @PutMapping("/{event_id}")
    public void putEvent(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String event,
            @RequestParam(defaultValue = "false") boolean isFileUpdate,
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        EventWithValidateDTO newEventDTO = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(event);
            newEventDTO = modelMapper.map(json, EventWithValidateDTO.class);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        eventService.putEventDTO(newEventDTO, event_id, user, isFileUpdate, file);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        User user = getUserFromEmail(tokenEmail);

        eventService.deleteEventDTOById(event_id, user);
    }

    private User getUserFromEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        }catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email from token " + email + " not found or does not exist");
        }
    }

    @GetMapping("/timeframe")
    public List<EventTimeframeDTO> getEventTimeframes(
            @RequestParam String date,
            @RequestParam Integer categoryId) {
        return eventService.getEventTimeframeDTOs(date, categoryId, true);
    }
}
