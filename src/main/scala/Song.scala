package uk.ac.ucl.rsdg.pronouncing

class Song(val title: String, val author: String, val lyrics: Map[Int, String]) {
  override def toString = "title: " + title
}

