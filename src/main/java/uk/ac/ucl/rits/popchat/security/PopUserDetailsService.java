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

/**
 * Manage the interface between our User database and the Spring Security API.
 *
 * <p>
 * Provides Spring the ability to look up users by password, so it can check
 * what privileges they have and if their password is correct
 *
 * @author RSDG
 *
 */
@Service
public class PopUserDetailsService implements UserDetailsService {

    /**
     * The repository of all users.
     */
    private final UserRepository userRepo;

    /**
     * Create a new PopUserDetailsService.
     *
     * @param userRepo Interface to the user table
     */
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
        // Everyone has basic access privileges
        authorities.add(() -> "ROLE_BASIC");
        if (user.getIsAdmin()) {
            // Admins get admin as an extra role
            authorities.add(() -> "ROLE_ADMIN");
        }
        // Credential expiration / lockout has not been implemented
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
    }

}
