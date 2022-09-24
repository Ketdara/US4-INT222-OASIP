package sit.int221.us4backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.services.EventService;
import sit.int221.us4backend.services.UserService;
import sit.int221.us4backend.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("")
    public Page<EventPartialDTO> getEventsAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOsAsPage(page, pageSize, tokenEmail);
    }

    @GetMapping("/category")
    public Page<EventPartialDTO> getEventsAllByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer categoryId,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOsByCategoryAsPage(page, pageSize, categoryId, tokenEmail);
    }

    @GetMapping("/upcoming")
    public Page<EventPartialDTO> getEventsAllByUpcoming(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOsByUpcomingAsPage(page, pageSize, now, tokenEmail);
    }

    @GetMapping("/past")
    public Page<EventPartialDTO> getEventsAllByPast(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOsByPastAsPage(page, pageSize, now, tokenEmail);
    }

    @GetMapping("/date")
    public Page<EventPartialDTO> getEventsAllByDate(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String date,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOsByDateAsPage(page, pageSize, date, tokenEmail);
    }

//    @GetMapping("/timeframe")
//    public List<EventTimeframeDTO> getEventTimeframes(
//            @RequestParam String date,
//            @RequestParam Integer categoryId) {
//        return eventService.getEventTimeframeDTOs(date, categoryId, true);
//    }

    @GetMapping("/{event_id}")
    public EventWithValidateDTO getEventById(
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        return eventService.getEventDTOById(event_id, tokenEmail, tokenRole);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEvent(
            @RequestBody EventWithValidateDTO newEventDTO,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        eventService.postEventDTO(newEventDTO, tokenEmail, tokenRole);
    }

    @PutMapping("/{event_id}")
    public void putEvent(
            @RequestBody EventWithValidateDTO newEventDTO,
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        eventService.putEventDTO(newEventDTO, event_id, tokenEmail, tokenRole);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(
            @PathVariable Integer event_id,
            HttpServletRequest request) {

        jwtTokenUtil.validateTokenFromHeader(request);
        String tokenEmail = jwtTokenUtil.getEmailFromHeader(request);
        String tokenRole = getRoleFromEmail(tokenEmail);

        eventService.deleteEventDTOById(event_id, tokenEmail, tokenRole);
    }

    private String getRoleFromEmail(String email) {
        try {
            return userRepository.findByEmail(email).getRole();
        }catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email from token " + email + " not found or does not exist");
        }
    }
}
