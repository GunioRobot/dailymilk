package dailymilk.i18n

import io.Source

/**
 * Extracts properties from files and put them to CSV file
 */

case class FilenameHolder(base: String, languages: List[String]) extends Traversable[String] {

  def baseFilename = composeFilename(base)

  def foreach[T](f: String => T) = {
    val names = baseFilename :: languages.map(composeFilename(base, _))
    for (x <- names) {
      f(x)
    }
  }

  private def composeFilename(parts: String*) = parts.mkString("_") + ".properties"
}

class PropertyFileCombinator(holder: FilenameHolder) {

  def loadToList(filename: String) = Source.fromPath(filename).getLines().toList

  def loopy = {
    for (line <- loadToList(holder.baseFilename)) {
      line match {
        case s: String if (s.startsWith("#")) => new LabelItem(s.drop(1).trim)
        case s: String if (s.trim.isEmpty) => EmptyItem 
      }
    }
  }
}

object PropertiesExtractingApp {

  def paths = ""
  def propertyBases = List("labels", "calculation", "combination", "importer", "Resource")
  def languages = List("en", "de", "sv", "nl")
  def defaultLang = "en"

  def main(args: Array[String]) {
    val filenames = for (base <- propertyBases) yield new FilenameHolder(base, languages)
    println("filenames: " + filenames)

    val holder = filenames(0)
    for (x <- holder) {
      println(x)
    }
  }
}