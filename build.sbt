scalaVersion := "2.12.6"

name := "popchat"
organization := "uk.ac.ucl.rits"
version := "0.1"

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.1.0",
    "de.sciss" % "sphinx4-core" % "1.0.0",
    "de.sciss" % "sphinx4-core" % "1.0.0",
    "com.typesafe.akka" %% "akka-http" % "10.1.3",
    "com.typesafe.akka" %% "akka-stream" % "2.5.14",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
    // Libraries for akka testing
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3",
    // Use Quill for DB interaction with PostgreSQL
    "org.postgresql" % "postgresql" % "9.4.1208",
    "io.getquill" %% "quill-jdbc" % "2.5.4"
)

// Context definition to connect Quill with Postgres
//lazy val ctx = new PostgresJdbcContext(PopChat, "ctx")

// Here, `libraryDependencies` is a set of dependencies, and by using `+=`,
// we're adding the cats dependency to the set of dependencies that sbt will go
// and fetch when it starts up.
// Now, in any Scala file, you can import classes, objects, etc, from cats with
// a regular import.

// TIP: To find the "dependency" that you need to add to the
// `libraryDependencies` set, which in the above example looks like this:

// "org.typelevel" %% "cats-core" % "1.1.0"

// You can use Scaladex, an index of all known published Scala libraries. There,
// after you find the library you want, you can just copy/paste the dependency
// information that you need into your build file. For example, on the
// typelevel/cats Scaladex page,
// https://index.scala-lang.org/typelevel/cats, you can copy/paste the sbt
// dependency from the sbt box on the right-hand side of the screen.

// IMPORTANT NOTE: while build files look _kind of_ like regular Scala, it's
// important to note that syntax in *.sbt files doesn't always behave like
// regular Scala. For example, notice in this build file that it's not required
// to put our settings into an enclosing object or class. Always remember that
// sbt is a bit different, semantically, than vanilla Scala.

// ================================
// Download reference data files
lazy val downloadFromZip = taskKey[Unit]("Download the cmu dictionary file")

import scala.sys.process._

downloadFromZip := {
    val targetfile: String = "cmudict-0.7b"
    val sourcefile: String = "http://svn.code.sf.net/p/cmusphinx/code/trunk/cmudict/cmudict-0.7b"
    if(java.nio.file.Files.notExists(new File(targetfile).toPath())) {
        println("Path does not exist, downloading...")
        new URL(sourcefile) #> new File(targetfile) !!
    } else {
        println("CMU dictionary exists, skipping download.")
    }
}

compile in Compile := ((compile in Compile) dependsOn downloadFromZip).value

// ============================================================================

// Most moderately interesting Scala projects don't make use of the very simple
// build file style (called "bare style") used in this build.sbt file. Most
// intermediate Scala projects make use of so-called "multi-project" builds. A
// multi-project build makes it possible to have different folders which sbt can
// be configured differently for. That is, you may wish to have different
// dependencies or different testing frameworks defined for different parts of
// your codebase. Multi-project builds make this possible.

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "ch.epfl.scala",
//       scalaVersion := "2.12.6"
//     )),
//     name := "hello-world"
//   )

// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html
