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

import uk.ac.ucl.rits.popchat.messages.BatchUserSpecification;
import uk.ac.ucl.rits.popchat.messages.NewUser;
import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

@RestController
@RequestMapping("/user")
public class UserEndpoints {

	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	private static final Logger log = LoggerFactory.getLogger(UserEndpoints.class);

	@Autowired
	public UserEndpoints(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/logout")
	public void logout() {
		log.info("Logout attempt!");
	}

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

	@PostMapping("/batch-signup")
	public List<NewUser> batchSignup(@RequestBody BatchUserSpecification users) {
		List<NewUser> generatedUsers = new ArrayList<>();
		for (int i = 0; i < users.getNumUsers(); i++) {
			String username = String.format("%s%d", users.getPrefix(), i + 1);
			int passVal = (int) (Math.random() * (Integer.MAX_VALUE)); // 4 byte number
			String password = String.format("%08X", passVal);
			NewUser generatedUser = new NewUser(username, password);
			this.signup(generatedUser);
			generatedUsers.add(generatedUser);
		}
		return generatedUsers;
	}

}
