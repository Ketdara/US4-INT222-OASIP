package sit.int221.us4backend.constraints;

import sit.int221.us4backend.validators.PastStringValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PastStringValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface PastString {
    String message() default "Cannot book event in the past; ";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
