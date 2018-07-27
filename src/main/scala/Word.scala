package uk.ac.ucl.rsdg.pronouncing

class Word(val word: String, val phones: String) {
    override def toString = word + ": " + phones
}
