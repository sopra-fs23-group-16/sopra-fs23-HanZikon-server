package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;


    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE.getStatus());
        String randomIcon = generateRandomIcon();
        newUser.setIcon(randomIcon); // set default icon
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public String generateRandomIcon(){
        String[] valuesArray = {"dog", "cat", "seelion", "cattle", "owl"};

        Random random = new Random();
        int randomIndex = random.nextInt(valuesArray.length);
        String randomValue = valuesArray[randomIndex];

        return randomValue;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @throws ResponseStatusException
     * @see User
     */

    public boolean checkIfUserIDExists(long userID) {
        User existedUser = userRepository.findById(userID);
        if (existedUser != null) {
            return true;
        } else {
            return false;
        }
    }

    private void checkIfUserExists(User newUser) {
        User existedUser = userRepository.findByUsername(newUser.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (existedUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }


    public User getUserById(long userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This ID cannot be found.");
    }

    public User getUserByToken(String token) {return userRepository.findByToken(token);}

    public void setStatus(long userId, String status) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getId() == userId) {
                user.setStatus(status);
                userRepository.save(user);
                break;
            }
        }
    }

    public User setting(User user) {
        user = userRepository.save(user);
        userRepository.flush();
        user = getUserById(user.getId());
        return user;
    }
}
