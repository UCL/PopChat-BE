package uk.ac.ucl.rits.popchat;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            throw new IllegalArgumentException("So such song " + songId);
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
}
