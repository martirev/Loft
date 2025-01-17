package springboot.restserver;

import core.User;
import core.Workout;
import filehandling.DirectLoftAccess;
import filehandling.LoftAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the REST controller for the Loft service.
 * It handles HTTP requests related to the Loft service.
 */
@RestController
@RequestMapping(LoftController.LOFT_SERVICE_PATH)
public class LoftController {
    public static final String LOFT_SERVICE_PATH = "loft";

    private static final Logger LOG = LoggerFactory.getLogger(LoftController.class);

    private LoftAccess access = new DirectLoftAccess();

    @GetMapping()
    public boolean isAlive() {
        LOG.debug("Checking if Loft is alive");
        return true;
    }

    /**
     * Registers a new user with the given name, username, password, and email.
     *
     * @param name     the name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email    the email of the user
     */
    @PostMapping(path = "users/{username}/register")
    public boolean registerUser(@PathVariable String username,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String email) {
        LOG.debug("Registering user: " + username);
        User user = new User(name, username, password, email);
        return access.registerUser(user);
    }

    /**
     * Saves a workout for a user.
     *
     * @param name     the name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email    the email of the user
     * @param workout  the workout to save
     */
    @PutMapping(path = "users/{username}/workouts")
    public boolean saveWorkout(@PathVariable String username,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String email,
            @RequestBody Workout workout) {
        LOG.debug("Adding workout to user: " + username);
        User user = new User(name, username, password, email);
        return access.writeWorkoutToUser(workout, user);
    }

    /**
     * Retrieves a user with the given username and password.
     *
     * @param username the username of the user to retrieve
     * @param password the password of the user to retrieve
     * @return the User object representing the retrieved user
     */
    @GetMapping(path = "users/{username}")
    @ResponseBody
    public User getUser(@PathVariable String username,
            @RequestParam String password) {
        LOG.debug("Getting user: " + username);
        return access.getUser(username, password);
    }

    /**
     * Checks if a username exists in the system.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    @GetMapping(path = "check-username/{username}")
    @ResponseBody
    public boolean usernameExists(@PathVariable String username) {
        LOG.debug("Checking if username exists: " + username);
        return access.usernameExists(username);
    }

    /**
     * Updates user information for the given username.
     *
     * @param username    the username of the user to update
     * @param name        the name of the user
     * @param password    the password of the user
     * @param email       the email of the user
     * @param newUsername the new username for the user
     * @param newName     the new name for the user
     * @param newPassword the new password for the user
     * @param newEmail    the new email for the user
     * @return true if the update was successful, false otherwise
     */
    @PutMapping(path = "users/{username}")
    public boolean updateUserInfo(@PathVariable String username,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String newUsername,
            @RequestParam String newName,
            @RequestParam String newPassword,
            @RequestParam String newEmail) {
        LOG.debug("Updating user: " + username + " to: " + newUsername);
        User oldUser = new User(name, username, password, email);
        User newUser = new User(newName, newUsername, newPassword, newEmail);
        return access.updateUserInfo(oldUser, newUser);
    }
}
