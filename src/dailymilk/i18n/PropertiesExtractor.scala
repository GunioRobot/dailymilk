package dailymilk.i18n

import io.Source
import scala.util.control.Exception._

/**
 * Extracts key/value pairs from property files.
 * Unifies keys and writes output to file
 */

object PropertiesExtractor {

  private val indexOutOfBounds = catching(classOf[IndexOutOfBoundsException])

  def printingClosure(lineNo: Int, columns: Array[String]) = {
    println(lineNo + " | " + columns.mkString("[", ", ", "]"))
  }

  def main(args: Array[String]) {
      import collection.mutable.Set
      import collection.mutable.HashSet
      import CsvParser._

      def keyExtractingClosure(keys: Set[String])(lineNo: Int, columns: Array[String]) : Unit = {
        indexOutOfBounds.opt {
          if (columns(0).contains("."))
            keys.add(columns(0))
        }
      }

      val keys = new HashSet[String]
      Source.fromPath(args(0)).parseCsv(keyExtractingClosure(keys)_)

      keys.foreach(println(_))
  }
}