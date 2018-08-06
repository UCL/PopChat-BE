package uk.ac.ucl.rsdg.pronouncing

import edu.cmu.sphinx.api._

import scala.io.Source
import java.io._
import scala.collection.mutable.{ ArrayBuffer, HashMap, MultiMap, Set }
import scala.collection.immutable.{Set => ImmSet}

/**
 * This class stores stores all known words broken up into their
 * phonemes.
 */
object Rhymes {
  type WordMap = HashMap[String, Set[String]] with MultiMap[String, String]

  val pronunciations: List[Word] = parse_cmu
  val lookup: WordMap = list_to_multimap(pronunciations)
  val rhyme_lookup = init_rhyme_map(pronunciations)

  /**
   * Open the CMU language dictionary and read it into a
   * Word array
   */
  def parse_cmu_speech(): ArrayBuffer[Word] = {
    // Open the english language dictionary from inside of the relevant JAR file
    val cldr = this.getClass.getClassLoader
    val filename = "edu/cmu/sphinx/models/en-us/cmudict-en-us.dict"
    val stream = new BufferedReader(new InputStreamReader(cldr.getResourceAsStream(filename)))
    // Prepare an array of words to store the data
    val words = new ArrayBuffer[Word]
    // for each line, parse it into a word
    var line = stream.readLine
    while (line != null){
      line = line.trim
      val data = line.split("  ")
      if (line.contains("0")) println(line)
      // Remove the number of the word (for words that have multiple pronunciations)
      val word = new Word(data(0).replaceAll("\\([0-9]+\\)$", "").toLowerCase, data(1))
      words.append(word)
      line = stream.readLine
    }
    stream.close()
    return words
  }

  /**
   * Open the CMU language dictionary and read it into a
   * Word array
   */
  def parse_cmu(): List[Word] = {
    val filename = "cmudict-0.7b"
    val stream = Source.fromFile(filename)("iso-8859-1")

    // for each line, parse it into a word
    val words = stream.getLines.filter( line => !line.startsWith(";")).
      map( line => {
        val data = line.trim.split("  ")
        require(data.length == 2)
        // Remove the number of the word (for words that have multiple pronunciations)
        new Word(data(0).replaceAll("\\([0-9]+\\)$", "").toLowerCase, data(1))
      }).toList

    stream.close()
    return words
  }


  def list_to_multimap(list: List[Word]): WordMap = {
    val word_map = new HashMap[String, Set[String]] with MultiMap[String, String]
    list.foreach({ word => word_map.addBinding(word.word, word.phones) })
    return word_map
  }

  def init_rhyme_map(list: List[Word]): WordMap = {
    val word_map = new HashMap[String, Set[String]] with MultiMap[String, String]
    list.foreach(word => {
      word_map.addBinding(rhyming_part(word.phones), word.word)
    })

    return word_map
  }

  def rhyming_part(phones: String): String = {
    val phones_list = phones.split(" ")
    val rhyming_start = phones_list.lastIndexWhere( phone => phone.endsWith("1") || phone.endsWith("2"))
    if (rhyming_start == -1) return phones
    return phones_list.slice(rhyming_start, phones_list.length).mkString(" ")
  }

  def get_phones_for_word(lookup: WordMap, word: String): Set[String] = lookup.get(word) getOrElse (Set.empty)


  def rhymes(word: String): ImmSet[String] = {
    get_phones_for_word(lookup, word).
      map(word => rhyming_part(word)).
      flatMap(word => rhyme_lookup.get(word) getOrElse Set.empty).
      filter(_ != word).
      toSet
  }


}

