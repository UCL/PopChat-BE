package uk.ac.ucl.rits.popchat.game;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository to act as an interface to the answers tables.
 *
 * @author RSDG
 *
 */
public interface SongGameQuestionOptionRepository extends CrudRepository<SongGameQuestionOption, Integer> {

}
