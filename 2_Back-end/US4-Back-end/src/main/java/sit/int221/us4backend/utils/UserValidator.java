package sit.int221.us4backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import sit.int221.us4backend.dtos.UserPartialDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

public class UserValidator {
    @Autowired
    private Validator validator;

    private static final UserValidator userValidator = new UserValidator();
    private UserValidator() { }
    public static UserValidator getInstance() { return userValidator; }

    public String annotationValidate(Object userDTO) {
        Set<ConstraintViolation<Object>> violations = validator.validate(userDTO);
        if(violations.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<Object> constraintViolation : violations) {
            stringBuilder.append(constraintViolation.getMessage());
        }
        return stringBuilder.toString();
    }

    public String uniqueNameValidate(String name, Integer id, List<UserPartialDTO> userDTOs) {
        if(name == null) return "";

        boolean isUnique = !userDTOs.stream().anyMatch(userDTO -> {
            if(id != null && id.equals(userDTO.getId())) return false;
            return name.equals(userDTO.getName());
        });
        if(isUnique) return "";
        return "User name must be unique; ";
    }

    public String uniqueEmailValidate(String email, Integer id, List<UserPartialDTO> userDTOs) {
        if(email == null) return "";

        boolean isUnique = !userDTOs.stream().anyMatch(userDTO -> {
            if(id != null && id.equals(userDTO.getId())) return false;
            return email.equals(userDTO.getEmail());
        });
        if(isUnique) return "";
        return "User email must be unique; ";
    }

}
