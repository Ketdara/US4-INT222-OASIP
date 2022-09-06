package sit.int221.us4backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.dtos.*;
import sit.int221.us4backend.entities.User;
import sit.int221.us4backend.model.JwtResponse;
import sit.int221.us4backend.repositories.UserRepository;
import sit.int221.us4backend.utils.JwtTokenUtil;
import sit.int221.us4backend.utils.ListMapper;
import sit.int221.us4backend.utils.UserValidator;

import javax.transaction.Transaction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private Argon2PasswordEncoder argon2Encoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private Sort sort = Sort.by("name").ascending();

    public Page<UserPartialDTO> getUserDTOsAsPage(Integer page, Integer pageSize) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, pageSize, sort));

        return userPage.map(entity -> modelMapper.map(entity, UserPartialDTO.class));
    }

    public List<UserPartialDTO> getUserDTOsAsList() {
        List<User> userList = userRepository.findAll();

        return listMapper.mapList(userList, UserPartialDTO.class, modelMapper);
    }

    public UserFullDTO getUserDTOById(Integer user_id) {
        User user = userRepository.findById(user_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + user_id + " not found or does not exist."));

        return modelMapper.map(user, UserFullDTO.class);
    }

    public void postUserDTO(UserWithValidateDTO newUserDTO) {
        trimUserField(newUserDTO);
        callUserValidator(newUserDTO, false);

        String hashedPassword = argon2Encoder.encode(newUserDTO.getPassword());

        userRepository.createUser(newUserDTO.getName(), newUserDTO.getEmail(), newUserDTO.getRole(), hashedPassword);
    }

    public void putUserDTO(UserWithValidateDTO newUserDTO, Integer user_id) {
        trimUserField(newUserDTO);
        callUserValidator(newUserDTO, true);

        userRepository.updateUser(newUserDTO.getName(), newUserDTO.getEmail(), newUserDTO.getRole(), user_id);
    }

    private UserWithValidateDTO trimUserField(UserWithValidateDTO newUserDTO) {
        if(newUserDTO.getName() != null) newUserDTO.setName(newUserDTO.getName().trim());
        if(newUserDTO.getEmail() != null) newUserDTO.setEmail(newUserDTO.getEmail().trim());
        if(newUserDTO.getRole() != null) newUserDTO.setRole(newUserDTO.getRole().trim());
        return newUserDTO;
    }

    private User mapUser(User oldUser, User newUser) {
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setRole(newUser.getRole());
        return oldUser;
    }

    public void deleteUserDTOById(Integer user_id) {
        userRepository.findById(user_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + user_id + " not found or does not exist."));
        userRepository.deleteById(user_id);
    }

    private void callUserValidator(UserWithValidateDTO newUserDTO, boolean isPut) {
        StringBuilder violationStringBuilder = new StringBuilder();
        violationStringBuilder.append(userValidator.annotationValidate(newUserDTO));

        if(newUserDTO.getName() != null || newUserDTO.getEmail() != null) {
            List<UserPartialDTO> userDTOs = getUserDTOsAsList();

            if(newUserDTO.getName() != null) {
                violationStringBuilder.append(userValidator.uniqueNameValidate(newUserDTO, userDTOs));
            }

            if(newUserDTO.getEmail() != null) {
                violationStringBuilder.append(userValidator.uniqueEmailValidate(newUserDTO, userDTOs));
            }
        }

        String violationString = violationStringBuilder.toString();
        if(violationString.length() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not valid; " + violationString);
    }

    public void authenticateCredentials(CredentialsDTO userCredentials) {
        trimCredentials(userCredentials);
        credentialsValidate(userCredentials);

        try {
            User user = userRepository.findByEmail(userCredentials.getEmail());

            if(!argon2Encoder.matches(userCredentials.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect.");
            }

        }catch(NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email " + userCredentials.getEmail() + " not found or does not exist.");
        }
    }

    private void credentialsValidate(CredentialsDTO userCredentials) {
        StringBuilder violationStringBuilder = new StringBuilder();
        violationStringBuilder.append(userValidator.credentialsAnnotationValidate(userCredentials));

        String violationString = violationStringBuilder.toString();
        if(violationString.length() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input not valid; " + violationString);
    }

    private CredentialsDTO trimCredentials(CredentialsDTO userCredentials) {
        if(userCredentials.getEmail() != null) userCredentials.setEmail(userCredentials.getEmail().trim());
        return userCredentials;
    }

    public ResponseEntity<?> loginCredentials(CredentialsDTO userCredentials) {
        trimCredentials(userCredentials);
        credentialsValidate(userCredentials);

        try {
            User user = userRepository.findByEmail(userCredentials.getEmail());

            if(!argon2Encoder.matches(userCredentials.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect.");
            }

        }catch(NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email " + userCredentials.getEmail() + " not found or does not exist.");
        }

        final String token = jwtTokenUtil.generateToken(userCredentials.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
