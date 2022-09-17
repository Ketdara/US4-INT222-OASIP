package sit.int221.us4backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.us4backend.dtos.EventCategoryDTO;
import sit.int221.us4backend.services.EventCategoryService;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {
    @Autowired
    EventCategoryService eventCategoryService;

    @GetMapping("")
    public List<EventCategoryDTO> getEventCategories() {
        return eventCategoryService.getEventCategoryDTOs();
    }

    @PutMapping("/{eventCategory_id}")
    public void putEvent(@RequestBody EventCategoryDTO newEventCategoryDTO, @PathVariable Integer eventCategory_id) {
        eventCategoryService.putEventCategoryDTO(newEventCategoryDTO, eventCategory_id);
    }
}
