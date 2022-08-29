package sit.int221.us4backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.us4backend.dtos.CredentialsDTO;
import sit.int221.us4backend.dtos.UserFullDTO;
import sit.int221.us4backend.dtos.UserPartialDTO;
import sit.int221.us4backend.dtos.UserWithValidateDTO;
import sit.int221.us4backend.services.UserService;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public Page<UserPartialDTO> getUsersAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserDTOsAsPage(page, pageSize);
    }

    @GetMapping("/{user_id}")
    public UserFullDTO getUserById(@PathVariable Integer user_id) {
        return userService.getUserDTOById(user_id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postUser(@RequestBody UserWithValidateDTO newUserDTO) {
        userService.postUserDTO(newUserDTO);
    }

    @PutMapping("/{user_id}")
    public void putUser(@RequestBody UserWithValidateDTO newUserDTO, @PathVariable Integer user_id) {
        userService.putUserDTO(newUserDTO, user_id);
    }

    @DeleteMapping("/{user_id}")
    public void deleteUser(@PathVariable Integer user_id) {
        userService.deleteUserDTOById(user_id);
    }

    @PostMapping("/match")
    public void authenticateUser(@RequestBody CredentialsDTO userCredentials) {
        userService.authenticateCredentials(userCredentials);
    }
}
