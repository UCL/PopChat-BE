import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._

import uk.ac.ucl.rsdg.api.WebServer

class EndpointsTest extends WordSpec with Matchers with ScalatestRouteTest {

    "The rhyme service" should {
        "return a result for raquel" in {
            Get("/words/rhymes-with/raquel") ~> WebServer.route ~> check {
                status shouldEqual StatusCodes.OK
                responseAs[String] should include ("michelle")
            }
        }
    }

    "The song service" should {
        "return a result for song with id 1" in {
            Get("/songs/song/1") ~> WebServer.route ~> check {
                status shouldEqual StatusCodes.OK
                //responseAs[String] should include ("michelle")
            }
        }
    }
}