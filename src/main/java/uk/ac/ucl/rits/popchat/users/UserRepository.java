package uk.ac.ucl.rits.popchat.users;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<PopUser, Long> {

	public PopUser findByUsername(String username);

}
