package sit.int221.us4backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import sit.int221.us4backend.dtos.UserWithValidateDTO;

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

    public String annotationValidate(UserWithValidateDTO newUserDTO) {
        Set<ConstraintViolation<UserWithValidateDTO>> violations = validator.validate(newUserDTO);
        if(violations.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<UserWithValidateDTO> constraintViolation : violations) {
            stringBuilder.append(constraintViolation.getMessage());
        }
        return stringBuilder.toString();
    }

    public String uniqueNameValidate(UserWithValidateDTO newUserDTO, List<UserWithValidateDTO> userDTOs) {
        if(newUserDTO.getName() == null) return "";

        boolean isUnique = !userDTOs.stream().anyMatch(userDTO -> {
            if(newUserDTO.getId() != null && newUserDTO.getId().equals(userDTO.getId())) return false;
            return newUserDTO.getName().equals(userDTO.getName());
        });
        if(isUnique) return "";
        return "User name must be unique; ";
    }

    public String uniqueEmailValidate(UserWithValidateDTO newUserDTO, List<UserWithValidateDTO> userDTOs) {
        if(newUserDTO.getEmail() == null) return "";

        boolean isUnique = !userDTOs.stream().anyMatch(userDTO -> {
            if(newUserDTO.getId() != null && newUserDTO.getId().equals(userDTO.getId())) return false;
            return newUserDTO.getEmail().equals(userDTO.getEmail());
        });
        if(isUnique) return "";
        return "User email must be unique; ";
    }

//    public String putFieldValidate(UserWithValidateDTO newUser, UserWithValidateDTO oldUser) {
//        boolean isSameCreatedTime = newUser.getName().equals(oldUser.getName());
//        boolean isSameUpdatedTime = newUser.getEmail().equals(oldUser.getEmail());
//        boolean isSameRole = newUser.getRole() == oldUser.getRole();
//        if(isSameName && isSameEmail && isSameRole) return "";
//        return "Cannot edit other field other than start time and notes; ";
//    }
}
