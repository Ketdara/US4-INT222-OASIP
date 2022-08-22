package sit.int221.us4backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import sit.int221.us4backend.dtos.EventWithValidateDTO;
import sit.int221.us4backend.dtos.EventTimeframeDTO;

import javax.validation.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EventValidator {
    @Autowired
    private DateTimeManager dateTimeManager;
    @Autowired
    private Validator validator;

    private static final EventValidator eventValidator = new EventValidator();
    private EventValidator() { }
    public static EventValidator getInstance() { return eventValidator; }

    public String annotationValidate(EventWithValidateDTO newEventDTO) {
        Set<ConstraintViolation<EventWithValidateDTO>> violations = validator.validate(newEventDTO);
        if(violations.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<EventWithValidateDTO> constraintViolation : violations) {
            stringBuilder.append(constraintViolation.getMessage());
        }
        return stringBuilder.toString();
    }

    public String overlapValidate(EventWithValidateDTO newEvent, List<EventTimeframeDTO> timeframes) {
        if(newEvent.getEventStartTime() == null) return "";
        if(newEvent.getEventDuration() == null) return "";

        String newISODateString = newEvent.getEventStartTime();
        Integer newDuration = newEvent.getEventDuration();

        Date newStartDate = dateTimeManager.ISOStringToDate(newISODateString);
        Date newEndDate = dateTimeManager.dateToEndDate(newStartDate, newDuration);

        boolean isOverlap = timeframes.stream().anyMatch(timeframe -> {
            if(newEvent.getId() != null && timeframe.getId().equals(newEvent.getId())){
                return false;
            }
            String oldISODateString = timeframe.getEventStartTime();
            Integer oldDuration = timeframe.getEventDuration();

            Date oldStartDate = dateTimeManager.ISOStringToDate(oldISODateString);
            Date oldEndDate = dateTimeManager.dateToEndDate(oldStartDate, oldDuration);

            return  newStartDate.compareTo(oldStartDate) == 0 ||
                    newEndDate.compareTo(oldEndDate) == 0 ||
                    (newStartDate.before(oldEndDate) && oldStartDate.before(newEndDate));
        });
        if(isOverlap) return "Event time overlapped with other event(s); ";
        return "";
    }

    public String putFieldValidate(EventWithValidateDTO newEvent, EventWithValidateDTO oldEvent) {
        boolean isSameName = newEvent.getBookingName().equals(oldEvent.getBookingName());
        boolean isSameEmail = newEvent.getBookingEmail().equals(oldEvent.getBookingEmail());
        boolean isSameDuration = newEvent.getEventDuration() == oldEvent.getEventDuration();
        boolean isSameCategory = sameCategoryValidate(newEvent, oldEvent);
        if(isSameName && isSameEmail && isSameDuration && isSameCategory) return "";
        return "Cannot edit other field other than start time and notes; ";
    }

    private boolean sameCategoryValidate(EventWithValidateDTO newEvent, EventWithValidateDTO oldEvent) {
        if( newEvent.getEventCategory().getId() != oldEvent.getEventCategory().getId() ||
            newEvent.getEventCategory().getEventDuration() != oldEvent.getEventCategory().getEventDuration() ||
            !newEvent.getEventCategory().getEventCategoryName().equals(oldEvent.getEventCategory().getEventCategoryName())
        ) return false;

        String oldDesc = oldEvent.getEventCategory().getEventCategoryDescription();
        String newDesc = newEvent.getEventCategory().getEventCategoryDescription();

        if(newDesc == null && oldDesc == null) return true;
        if(newDesc == null && oldDesc != null || newDesc != null && oldDesc == null) return false;
        return newDesc.equals(oldDesc);
    }

    public String eventCategoryNullValidate(EventWithValidateDTO newEvent) {
        if(newEvent.getEventCategory() == null) return "Event Category cannot be null; ";
        return "";
    }
}
