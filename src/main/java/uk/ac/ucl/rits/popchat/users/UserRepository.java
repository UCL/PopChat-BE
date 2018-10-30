package uk.ac.ucl.rits.popchat.users;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserSecurity, Long> {

	public UserSecurity findByUsername(String username);

}
