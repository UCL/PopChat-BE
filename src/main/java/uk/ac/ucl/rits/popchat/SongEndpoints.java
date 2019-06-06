package uk.ac.ucl.rits.popchat;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.game.SongGame;
import uk.ac.ucl.rits.popchat.game.SongGameQuestionOption;
import uk.ac.ucl.rits.popchat.game.SongGameQuestionOptionRepository;
import uk.ac.ucl.rits.popchat.game.SongGameRepository;
import uk.ac.ucl.rits.popchat.game.SongGameResponse;
import uk.ac.ucl.rits.popchat.game.SongGameResponseRepository;
import uk.ac.ucl.rits.popchat.messages.Game;
import uk.ac.ucl.rits.popchat.messages.GameAnswer;
import uk.ac.ucl.rits.popchat.messages.SongAdditionResults;
import uk.ac.ucl.rits.popchat.messages.SongResponse;
import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;
import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

/**
 * Controller for song related endpoints.
 *
 * @author RSDG
 *
 */
@RestController
public class SongEndpoints {

    @Autowired
    private UserRepository                   userRepo;

    @Autowired
    private SongRepository                   songRepo;

    @Autowired
    private SongGameRepository               gameRepo;

    @Autowired
    private SongGameQuestionOptionRepository optionRepo;

    @Autowired
    private SongGameResponseRepository       responseRepo;

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

    /**
     * Generate a game for a given song.
     *
     * @param songId    Song to generate the game for
     * @param principal User generating the game
     * @return Game to generate
     */
    @PostMapping("/play/{songId}")
    public Game playGame(@PathVariable("songId") long songId, Principal principal) {
        Optional<Song> songSearch = songRepo.findById(songId);
        if (songSearch.isEmpty()) {
            throw new IllegalArgumentException("No such song " + songId);
        }
        Song song = songSearch.get();
        Lyrics lyrics = new Lyrics(song);
        SongGame game = new SongGame(song, lyrics);

        PopUser user = this.userRepo.findByUsername(principal.getName());
        if (user == null) {
            throw new RuntimeException("Cannot find user");
        }
        game.setUser(user);
        game = this.gameRepo.save(game);

        return new Game(game);
    }

    /**
     * Generate transient game for a given song with a specific number of question.
     *
     * @param songId    Song to generate the game for
     * @param number    Number of questions to generate
     * @return Game to generate
     */
    @GetMapping("/generate/{songId}")
    public Game playGame(@PathVariable("songId") long songId, @RequestParam("num") int number) {
        Optional<Song> songSearch = songRepo.findById(songId);
        if (songSearch.isEmpty()) {
            throw new IllegalArgumentException("No such song " + songId);
        }
        Song song = songSearch.get();
        Lyrics lyrics = new Lyrics(song);
        SongGame game = new SongGame(song, lyrics, number);

        return new Game(game);
    }

    /**
     * Respond to a question in a song game.
     *
     * @param response response to a game question
     * @param user     the user responding
     */
    @PostMapping("/answer")
    public void answer(@RequestBody GameAnswer response, Principal user) {
        PopUser popUser = userRepo.findByUsername(user.getName());
        if (popUser == null) {
            throw new RuntimeException("Cannot find current user");
        }
        Optional<SongGameQuestionOption> answer = optionRepo.findById(response.getOptionId());
        if (answer.isEmpty()) {
            throw new RuntimeException("Invalid answer selected");
        }
        SongGameResponse resp = new SongGameResponse();
        resp.setUser(popUser);
        resp.setQuestionOptionId(answer.get());
        resp.setQuestionStartTime(response.getStartTime());
        resp.setQuestionEndTime(response.getEndTime());

        this.responseRepo.save(resp);
    }

    /**
     * Set the details for a song. Editing them if necessary. A song is being edited
     * only if it has an ID.
     *
     * @param song Song details to update
     * @return Outcomes of attempting to insert the song
     */
    @PostMapping("/setSong")
    public SongAdditionResults setSong(@RequestBody Song song) {
        SongAdditionResults results = new SongAdditionResults();
        results.setValid(false);

        List<Song> sameTitle = this.songRepo.findByTitleAndArtistIgnoreCase(song.getTitle(), song.getArtist());

        if (song.getYear() < 1) {
            results.setYearErrorMessage("You must have a valid year (>=1)");
            return results;
        }

        if (song.getId() == 0 && !sameTitle.isEmpty()) {
            results.setTitleErrorMessage("This combination of title and artist already exist");
            results.setArtistErrorMessage("This combination of title and artist already exist");
            return results;
        }

        if (song.getId() == 0 && this.songRepo.findFirst1ByVideo(song.getVideo()) != null) {
            results.setVideoErrorMessage("A song with this video already exists in the database");
            return results;
        }

        try {
            new Lyrics(song);
        } catch (Exception e) {
            e.printStackTrace();
            results.setLyricsErrorMessage("LRC lyrics were invalid. Please check them");
            return results;
        }

        try {
            this.songRepo.save(song);
        } catch (Exception e) {
            results.setTitleErrorMessage(
                    "Song failed to save for an unknown reason. Please contact your administrator");
            return results;
        }

        // If all the checks pass then all is well
        results.setValid(true);
        return results;
    }

    /**
     * Get the full list of all songs.
     *
     * @return All the songs and their data.
     */
    @GetMapping("/viewSongs")
    public Iterable<Song> viewSongs() {
        return this.songRepo.findAll();
    }

    /**
     * Delete a song.
     *
     * @param songId Song to delete
     * @return Game to generate
     */
    @DeleteMapping("/song/{songId}")
    public Boolean deleteSong(@PathVariable("songId") long songId) {
        try {
            this.songRepo.deleteById(songId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
