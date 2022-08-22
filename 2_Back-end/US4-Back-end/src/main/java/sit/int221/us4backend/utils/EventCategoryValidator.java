package sit.int221.us4backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import sit.int221.us4backend.dtos.EventCategoryDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EventCategoryValidator {
    @Autowired
    private Validator validator;

    private static final EventCategoryValidator eventCategoryValidator = new EventCategoryValidator();
    private EventCategoryValidator() { }
    public static EventCategoryValidator getInstance() { return eventCategoryValidator; }

    public String annotationValidate(EventCategoryDTO newEventCategoryDTO) {
        Set<ConstraintViolation<EventCategoryDTO>> violations = validator.validate(newEventCategoryDTO);
        if(violations.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<EventCategoryDTO> constraintViolation : violations) {
            stringBuilder.append(constraintViolation.getMessage());
        }
        return stringBuilder.toString();
    }

    public String uniqueNameValidate(EventCategoryDTO newEventCategoryDTO, List<EventCategoryDTO> eventCategoryDTOs) {
        if(newEventCategoryDTO.getEventCategoryName() == null) return "";

        boolean isUnique = !eventCategoryDTOs.stream().anyMatch(eventCategoryDTO -> {
            if(newEventCategoryDTO.getId().equals(eventCategoryDTO.getId())){
                return false;
            }
            return newEventCategoryDTO.getEventCategoryName().equals(eventCategoryDTO.getEventCategoryName());
        });
        if(isUnique) return "";
        return "Category name must be unique; ";
    }
}
