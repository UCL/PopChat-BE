import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import spray.json._
import spray.json.DefaultJsonProtocol._

import uk.ac.ucl.rsdg.pronouncing.Rhymes

object WebServer {

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("words" / "rhymes-with" / Segment) { word =>
        get {
          val results = Rhymes.rhymes(word.toLowerCase)
          complete(HttpEntity(ContentTypes.`application/json`, results.toJson.toString))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}