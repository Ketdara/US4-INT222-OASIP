package sit.int221.us4backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.us4backend.dtos.*;
import sit.int221.us4backend.model.RefreshRequest;
import sit.int221.us4backend.services.UserService;
import sit.int221.us4backend.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("")
    public Page<UserPartialDTO> getUsersAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer pageSize,
            HttpServletRequest request) {
        jwtTokenUtil.validateTokenFromHeader(request);
        return userService.getUserDTOsAsPage(page, pageSize);
    }

    @GetMapping("/{user_id}")
    public UserFullDTO getUserById(@PathVariable Integer user_id, HttpServletRequest request) {
        jwtTokenUtil.validateTokenFromHeader(request);
        return userService.getUserDTOById(user_id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void postUser(@RequestBody UserPostDTO newUserDTO, HttpServletRequest request) {
        jwtTokenUtil.validateTokenFromHeader(request);
        userService.postUserDTO(newUserDTO);
    }

    @PutMapping("/{user_id}")
    public void putUser(@RequestBody UserPutDTO newUserDTO, @PathVariable Integer user_id, HttpServletRequest request) {
        jwtTokenUtil.validateTokenFromHeader(request);
        userService.putUserDTO(newUserDTO, user_id);
    }

    @DeleteMapping("/{user_id}")
    public void deleteUser(@PathVariable Integer user_id, HttpServletRequest request) {
        jwtTokenUtil.validateTokenFromHeader(request);
        userService.deleteUserDTOById(user_id);
    }

    @PostMapping("/match")
    public void authenticateUser(@RequestBody CredentialsDTO userCredentials) {
        userService.authenticateCredentials(userCredentials);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody CredentialsDTO userCredentials) {
        return userService.loginCredentials(userCredentials);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        jwtTokenUtil.validateRefreshTokenFromBody(request);
        return userService.regenerateTokens(request);
    }
}
