package sit.int221.us4backend.constraints;

import sit.int221.us4backend.validators.EnumRoleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumRoleValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumRole {
    Class<? extends Enum<?>> enumClass();
    String message() default "Invalid Role; ";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}