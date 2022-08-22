package sit.int221.us4backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.EventCategoryDTO;
import sit.int221.us4backend.entities.EventCategory;
import sit.int221.us4backend.repositories.EventCategoryRepository;
import sit.int221.us4backend.utils.EventCategoryValidator;
import sit.int221.us4backend.utils.ListMapper;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private EventCategoryValidator eventCategoryValidator;

    public List<EventCategoryDTO> getEventCategoryDTOs() {
        List<EventCategory> eventCategories = eventCategoryRepository.findAll();
        return listMapper.mapList(eventCategories, EventCategoryDTO.class, modelMapper);
    }

    public EventCategory putEventCategoryDTO(EventCategoryDTO newEventCategoryDTO, Integer eventCategory_id) {
        trimEventCategoryField(newEventCategoryDTO);
        callEventCategoryValidator(newEventCategoryDTO);
        EventCategory newEventCategory = modelMapper.map(newEventCategoryDTO, EventCategory.class);
        EventCategory eventCategory = eventCategoryRepository.findById(eventCategory_id).map(oldEventCategory -> mapEventCategory(oldEventCategory, newEventCategory))
                .orElseGet(() ->
                {
                    newEventCategory.setId(eventCategory_id);
                    return newEventCategory;
                });
        return eventCategoryRepository.saveAndFlush(eventCategory);
    }

    private EventCategoryDTO trimEventCategoryField(EventCategoryDTO newEventCategoryDTO) {
        if(newEventCategoryDTO.getEventCategoryName() != null) newEventCategoryDTO.setEventCategoryName(newEventCategoryDTO.getEventCategoryName().trim());
        if(newEventCategoryDTO.getEventCategoryDescription() != null) newEventCategoryDTO.setEventCategoryDescription(newEventCategoryDTO.getEventCategoryDescription().trim());
        return newEventCategoryDTO;
    }

    private EventCategory mapEventCategory(EventCategory oldEventCategory, EventCategory newEventCategory) {
        oldEventCategory.setEventCategoryName(newEventCategory.getEventCategoryName());
        oldEventCategory.setEventCategoryDescription(newEventCategory.getEventCategoryDescription());
        oldEventCategory.setEventDuration(newEventCategory.getEventDuration());
        return oldEventCategory;
    }

    private void callEventCategoryValidator(EventCategoryDTO newEventCategoryDTO) {
        StringBuilder violationStringBuilder = new StringBuilder();
        violationStringBuilder.append(eventCategoryValidator.annotationValidate(newEventCategoryDTO));

        if(newEventCategoryDTO.getEventCategoryName() != null) {
            List<EventCategoryDTO> eventCategoryDTOs = getEventCategoryDTOs();
            violationStringBuilder.append(eventCategoryValidator.uniqueNameValidate(newEventCategoryDTO, eventCategoryDTOs));
        }

        String violationString = violationStringBuilder.toString();
        if(violationString.length() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not valid; " + violationString);
    }
}
