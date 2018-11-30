package uk.ac.ucl.rits.popchat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.messages.SongResponse;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;

@RestController
public class SongEndpoints {

	@Autowired
	private SongRepository songRepo;

	/**
	 * Get the list of songs.
	 * 
	 * @param page    Which page to get. The first page is 0.
	 * @param perPage How many results to get per page
	 * @return A Page of Song results (incomplete information about the songs).
	 */
	@GetMapping("/songs")
	public Page<SongResponse> getSongs(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "perPage", defaultValue = "20") int perPage) {
		Pageable pageable = PageRequest.of(page, perPage);
		Page<Song> songs = this.songRepo.findAll(pageable);
		return songs.map(s -> new SongResponse(s));
	}

}
