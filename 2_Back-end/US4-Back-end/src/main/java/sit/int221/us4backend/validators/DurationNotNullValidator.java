package sit.int221.us4backend.validators;

import org.springframework.stereotype.Component;
import sit.int221.us4backend.constraints.DurationNotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class DurationNotNullValidator implements ConstraintValidator<DurationNotNull, Integer> {
    @Override
    public void initialize(DurationNotNull eventStartTime) {
    }

    @Override
    public boolean isValid(Integer contactField, ConstraintValidatorContext cxt) {
        return contactField != null;
    }
}
