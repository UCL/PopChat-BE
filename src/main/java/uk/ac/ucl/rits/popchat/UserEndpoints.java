package uk.ac.ucl.rits.popchat;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserEndpoints {

	@PostMapping("/login")
	public void login(String username, String password) {}
	
	@PostMapping("/logout")
	public void logout() {}
}
