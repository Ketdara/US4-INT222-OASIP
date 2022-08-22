package sit.int221.us4backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.us4backend.dtos.EventTimeframeDTO;
import sit.int221.us4backend.dtos.EventPartialDTO;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.services.EventService;

import java.util.List;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("")
    public Page<EventPartialDTO> getEventsAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return eventService.getEventDTOsAsPage(page, pageSize);
    }

    @GetMapping("/category")
    public Page<EventPartialDTO> getEventsAllByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer categoryId) {
        return eventService.getEventDTOsByCategoryAsPage(page, pageSize, categoryId);
    }

    @GetMapping("/upcoming")
    public Page<EventPartialDTO> getEventsAllByUpcoming(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now) {
        return eventService.getEventDTOsByUpcomingAsPage(page, pageSize, now);
    }

    @GetMapping("/past")
    public Page<EventPartialDTO> getEventsAllByPast(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String now) {
        return eventService.getEventDTOsByPastAsPage(page, pageSize, now);
    }

    @GetMapping("/date")
    public Page<EventPartialDTO> getEventsAllByDate(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam String date) {
        return eventService.getEventDTOsByDateAsPage(page, pageSize, date);
    }

    @GetMapping("/timeframe")
    public List<EventTimeframeDTO> getEventTimeframes(
            @RequestParam String date,
            @RequestParam Integer categoryId) {
        return eventService.getEventTimeframeDTOs(date, categoryId, true);
    }

    @GetMapping("/{event_id}")
    public EventWithValidateDTO getEventById(@PathVariable Integer event_id) {
        return eventService.getEventDTOById(event_id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEvent(@RequestBody EventWithValidateDTO newEventDTO) {
        eventService.postEventDTO(newEventDTO);
    }

    @PutMapping("/{event_id}")
    public void putEvent(@RequestBody EventWithValidateDTO newEventDTO, @PathVariable Integer event_id) {
        eventService.putEventDTO(newEventDTO, event_id);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(@PathVariable Integer event_id) {
        eventService.deleteEventDTOById(event_id);
    }
}
