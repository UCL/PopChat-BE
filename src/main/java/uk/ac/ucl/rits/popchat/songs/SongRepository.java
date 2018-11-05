package uk.ac.ucl.rits.popchat.songs;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface SongRepository extends PagingAndSortingRepository<Song, Long> {

	public Song findFirst1ByVideo(String video);

}
