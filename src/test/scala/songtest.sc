import org.scalatest._
import uk.ac.ucl.rsdg.pronouncing.Song

class SongTest extends FlatSpec with Matchers {

  // TODO: We probably need to use scalatest-json here
  "Song with id 1" should "have be titled Roar" in {
    Song.get("1").size should be > 0
  }

}

