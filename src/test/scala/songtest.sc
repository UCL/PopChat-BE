import org.scalatest._
import uk.ac.ucl.rsdg.pronouncing.Song

class SongTest extends FlatSpec with Matchers {

  // TODO: We probably need to use scalatest-json

  "Song with id 1" should "be titled Roar" in {
    Song.get("1").title should be "Roar"
  }

  "Author of song with id 1" should "be Katy Perry" in {
    Song.get("1").author should be "Katy Perry"
  }

}

