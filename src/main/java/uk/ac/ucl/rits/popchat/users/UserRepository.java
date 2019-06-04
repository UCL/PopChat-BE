package uk.ac.ucl.rits.popchat.users;

import org.springframework.data.repository.CrudRepository;

/**
 * Provides database operations to interact with the Pop Users.
 *
 * @author RSDG
 *
 */
public interface UserRepository extends CrudRepository<PopUser, Long> {

    /**
     * Find a PopUser by their username.
     *
     * @param username The username to search for
     * @return The PopUser with that username if one exists
     */
    PopUser findByUsername(String username);

}
