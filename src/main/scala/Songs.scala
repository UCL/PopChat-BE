package uk.ac.ucl.rsdg.pronouncing

object Songs {

  // TODO: Manage connection to DB

  def getSong(id: String): Song = {
    new Song("Roar", "Katy Perry",
             Map(15 -> "Patatin, patatan", 53 -> "Hander klander"))
  }
}
