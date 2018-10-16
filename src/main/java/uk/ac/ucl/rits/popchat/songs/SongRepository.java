package uk.ac.ucl.rits.popchat.songs;

import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, Long> {

	public Song findFirst1ByVideo(String video);

}
