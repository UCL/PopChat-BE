package uk.ac.ucl.rits.popchat.songs;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository to allow access to Songs in the database
 * 
 * @author RSDG
 *
 */
public interface SongRepository extends PagingAndSortingRepository<Song, Long> {

	/**
	 * Find the first Song that has the given video
	 * 
	 * @param video The URL for the video
	 * @return First song that has that video if one exists
	 */
	public Song findFirst1ByVideo(String video);

	/**
	 * Find songs with a given title
	 * 
	 * @param title The title to search for
	 * @return
	 */
	public List<Song> findByTitleIgnoreCase(String title);

}
