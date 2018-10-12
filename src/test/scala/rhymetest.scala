import org.scalatest._
import uk.ac.ucl.rsdg.pronouncing.Rhymes

class RhymeTest extends FlatSpec with Matchers {

    "Rhymes" should "find more than 1 rhyme for roma" in {
        Rhymes.rhymes("roma").size should be > 1
    }

    it should "be case insensitive" in {
        Rhymes.rhymes("Roma") should contain theSameElementsAs Rhymes.rhymes("roma")
        Rhymes.rhymes("Roma") should contain theSameElementsAs Rhymes.rhymes("RoMa")
    }

    it should "not find rhymes for elephant" in {
        Rhymes.rhymes("Elephant") shouldBe empty
    }

}