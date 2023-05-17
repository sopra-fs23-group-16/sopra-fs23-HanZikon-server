package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users/localUser")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getLocalUser(@RequestBody UserPostDTO userPostDTO) {
        User foundUser = userService.getUserByToken(userPostDTO.getToken());
        // convert internal representation of user back to API
        if(foundUser==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(foundUser);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@PathVariable long userId) {

        User userExist = userService.getUserById(userId);
        if(userExist==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(userExist);
        return userGetDTO;
    }

    @GetMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateStatus(@PathVariable long userId) {
        userService.setStatus(userId, UserStatus.OFFLINE.getStatus());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // change status
        List<User> users = userService.getUsers();
        for (User value : users) {
            if (userInput.getUsername().equals(value.getUsername())) {
                if (userInput.getPassword().equals(value.getPassword())) {
                    userService.setStatus(value.getId(), UserStatus.ONLINE.getStatus());
                }
            }
        }

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO checkUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        List<User> users = userService.getUsers();
        String baseErrorMessage = "The %s provided is wrong! You may want to create a new user account.";
        for (User value : users) {
            if (userInput.getUsername().equals(value.getUsername())) {
                if (userInput.getPassword().equals(value.getPassword())) {
                    userService.setStatus(value.getId(), UserStatus.ONLINE.getStatus());
                    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(value);
                }
                else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "password"));
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "username"));
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userUpdate(@RequestBody UserPostDTO userPostDTO, @PathVariable long userId) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User userExist = userService.getUserById(userId);

        String baseErrorMessage1 = "The %s provided is not found!";
        String baseErrorMessage2 = "The %s provided is already exist!";
        String defaultIcon = "dog";
        if (userExist==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage1, "user"));
        }

        if (userInput.getUsername()!=null && userInput.getUsername().length()>=1) {
            List<User> users = userService.getUsers();
            for (User value : users) {
                if (userInput.getUsername().equals(value.getUsername())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage2, "username"));
                }
            }
            userExist.setUsername(userInput.getUsername());
        }

        if (userInput.getPassword() != null) {
            userExist.setPassword(userInput.getPassword());
        }

        if (userInput.getIcon() != null) {
            userExist.setIcon(userInput.getIcon());
        } else {
            userExist.setIcon(defaultIcon);
        }

        userService.setting(userExist);
    }
}
