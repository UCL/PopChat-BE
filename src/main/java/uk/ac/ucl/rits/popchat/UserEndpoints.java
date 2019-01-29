package uk.ac.ucl.rits.popchat;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.atrox.haikunator.Haikunator;
import uk.ac.ucl.rits.popchat.messages.BatchUserSpecification;
import uk.ac.ucl.rits.popchat.messages.NewUser;
import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

/**
 * Manage endpoints that control user accounts.
 *
 * @author RSDG
 *
 */
@RestController
@RequestMapping("/user")
public class UserEndpoints {

    /**
     * User database.
     */
    private UserRepository      userRepo;
    /**
     * PasswordEncoder for hashing.
     */
    private PasswordEncoder     passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserEndpoints.class);

    /**
     * Create a UserEndpoints.
     *
     * @param userRepo        The users repository
     * @param passwordEncoder The password encoder
     */
    @Autowired
    public UserEndpoints(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user account. The new user must have a unique username, and will
     * not be an administrator
     *
     * @param user Specification of the new user to create
     */
    @PostMapping("/signup")
    public void signup(@RequestBody NewUser user) {
        PopUser existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Sorry, this username is already taken");
        }
        try {

            PopUser newUser = new PopUser(user.getUsername(), passwordEncoder.encode(user.getPassword()), false);
            userRepo.save(newUser);
        } catch (IllegalStateException e) {
            log.error("Failed to create new user", e);
            throw new RuntimeException("Sorry. New users cannot be created at this time. Please contact support.");
        }
    }

    /**
     * Create a large number of users in a single request.
     *
     * <p>
     * Note that as passwords are generated by this method, it returns them
     * unhashed.
     *
     * @param users The specification of what users to create
     * @return A list of Users (username & password) that have been created.
     */
    @PostMapping("/batch-signup")
    public List<NewUser> batchSignup(@RequestBody BatchUserSpecification users) {
        List<NewUser> generatedUsers = new ArrayList<>();
        Haikunator haik = new Haikunator();
        haik.setTokenLength(1);
        for (int i = 0; i < users.getNumUsers(); i++) {
            // Usernames are numerically sequential
            String username = String.format("%s%d", users.getPrefix(), i + 1);
            // Password are haikunated
            String password = haik.haikunate();
            NewUser generatedUser = new NewUser(username, password);
            this.signup(generatedUser);
            generatedUsers.add(generatedUser);
        }
        return generatedUsers;
    }

}
