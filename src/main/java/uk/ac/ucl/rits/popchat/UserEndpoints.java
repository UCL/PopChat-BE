package uk.ac.ucl.rits.popchat;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.messages.BatchUserSpecification;
import uk.ac.ucl.rits.popchat.messages.NewUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;
import uk.ac.ucl.rits.popchat.users.PopUser;

@RestController
@RequestMapping("/user")
public class UserEndpoints {

	private UserRepository userRepo;
	private static final Logger log = LoggerFactory.getLogger(UserEndpoints.class);

	private final String saltAlgorithm;
	private final String hashAlgorithm;
	private final int saltLength;
	private final int hashLength;
	private final int iterations;

	@Autowired
	public UserEndpoints(UserRepository userRepo, @Value("${salt.algorithm}") String saltAlgorithm,
			@Value("${hash.algorithm}") String hashAlgorithm, @Value("${salt.length}") int saltLength,
			@Value("${hash.length}") int hashLength, @Value("${hash.iterations}") int iterations) {
		this.userRepo = userRepo;
		this.saltAlgorithm = saltAlgorithm;
		this.hashAlgorithm = hashAlgorithm;
		this.hashLength = hashLength;
		this.saltLength = saltLength;
		this.iterations = iterations;
	}

	@PostMapping("/login")
	public boolean login(@RequestBody NewUser user) {
		PopUser existingUser = userRepo.findByUsername(user.getUsername());
		if (existingUser == null) {
			return false;
		}
		try {
			return existingUser.validatePassword(user.getPassword());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Failed to check password", e);
			return false;
		}
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
			PopUser newUser = PopUser.generateNewUser(user.getUsername(), user.getPassword(), hashAlgorithm,
					this.saltAlgorithm, iterations, saltLength, hashLength);
			userRepo.save(newUser);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
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