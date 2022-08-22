package sit.int221.us4backend.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.constraints.PastString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PastStringValidator implements ConstraintValidator<PastString, String> {
    @Override
    public void initialize(PastString eventStartTime) {
    }

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        if(contactField == null) return true;
        Date date;
        try {
            date = new Date(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(contactField).getTime() + (3600000 * 7));
        }catch(ParseException p) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date/Time format not supported");
        }
        return date.after(new Date());
    }
}