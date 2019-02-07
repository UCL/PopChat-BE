package uk.ac.ucl.rits.popchat.game;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository to act as an interface to the Games tables.
 *
 * @author RSDG
 *
 */
public interface SongGameRepository extends CrudRepository<SongGame, Integer> {

}
