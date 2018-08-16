import org.scalatest._
import uk.ac.ucl.rsdg.pronouncing.Rhymes

class RhymeTest extends FlatSpec with Matchers {

    "Roma" should "have more than 1 rhyme" in {
        assert(Rhymes.rhymes("roma").size > 1)
    }

    "Rhymes" should "be case insensitive" in {
        Rhymes.rhymes("Roma") should contain theSameElementsAs Rhymes.rhymes("roma")
        Rhymes.rhymes("Roma") should contain theSameElementsAs Rhymes.rhymes("RoMa")
    }

    "Elephant" should "not rhyme with anything" in {
        Rhymes.rhymes("Elephant").size shouldBe 0
    }

}