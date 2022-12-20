package sit.int221.us4backend.controllers;

import io.jsonwebtoken.Claims;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.entities.Event;
import sit.int221.us4backend.entities.User;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.services.EventService;
import sit.int221.us4backend.utils.DateTimeManager;
import sit.int221.us4backend.utils.EmailUtil;
import sit.int221.us4backend.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
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

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOsAsPage(page, pageSize, claims);
    }

    @GetMapping("/category")
    public Page<EventPartialDTO> getEventsAllByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer categoryId,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOsByCategoryAsPage(page, pageSize, categoryId, claims);
    }

    @GetMapping("/upcoming")
    public Page<EventPartialDTO> getEventsAllByUpcoming(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOsByUpcomingAsPage(page, pageSize, now, claims);
    }

    @GetMapping("/past")
    public Page<EventPartialDTO> getEventsAllByPast(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOsByPastAsPage(page, pageSize, now, claims);
    }

    @GetMapping("/date")
    public Page<EventPartialDTO> getEventsAllByDate(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String date,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOsByDateAsPage(page, pageSize, date, claims);
    }

    @GetMapping("/{event_id}")
    public EventWithValidateDTO getEventById(
            @PathVariable Integer event_id,
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        return eventService.getEventDTOById(event_id, claims);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEvent(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String event,
            HttpServletRequest request) {

        String token;
        Claims claims;
        try{
            token = jwtTokenUtil.extractTokenFromHeader(request);
            jwtTokenUtil.validateToken(token);
            claims = jwtTokenUtil.getAllClaimsFromToken(token);
        }catch(ResponseStatusException e) {
            claims = null;
        }

        EventWithValidateDTO newEventDTO = getEventFromString(event);

        Event postedEvent = eventService.postEventDTO(newEventDTO, claims, file);
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

    @PutMapping("/{event_id}")
    public void putEvent(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String event,
            @RequestParam(defaultValue = "false") boolean isFileUpdate,
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        EventWithValidateDTO newEventDTO = getEventFromString(event);

        eventService.putEventDTO(newEventDTO, event_id, claims, isFileUpdate, file);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        String token = jwtTokenUtil.extractTokenFromHeader(request);
        jwtTokenUtil.validateToken(token);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        eventService.deleteEventDTOById(event_id, claims);
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

    private EventWithValidateDTO getEventFromString(String eventString) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(eventString);
            return modelMapper.map(json, EventWithValidateDTO.class);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
