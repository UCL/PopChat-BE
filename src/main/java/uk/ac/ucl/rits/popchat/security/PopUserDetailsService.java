package uk.ac.ucl.rits.popchat.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

@Service
public class PopUserDetailsService implements UserDetailsService {

	private final UserRepository userRepo;

	@Autowired
	public PopUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PopUser user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("No such username " + username);
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(() -> "ROLE_BASIC");
		if (user.getIsAdmin()) {
			authorities.add(() -> "ROLE_ADMIN");
		}
		return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
	}

}
