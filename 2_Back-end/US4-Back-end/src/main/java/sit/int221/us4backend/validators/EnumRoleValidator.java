package sit.int221.us4backend.validators;

import org.springframework.stereotype.Component;
import sit.int221.us4backend.constraints.EnumRole;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EnumRoleValidator implements ConstraintValidator<EnumRole, String> {
    private List<String> acceptedValues;

    @Override
    public void initialize(EnumRole annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        if (contactField == null) {
            return true;
        }

        return acceptedValues.contains(contactField);
    }
}
