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

      def keyExtractingClosure(lineNo: Int, columns: Array[String]) : Option[String] = {
        indexOutOfBounds.opt[String]{
          if (columns(0).contains("."))
            columns(0)
          else throw new IndexOutOfBoundsException
        }
      }
      val keys = Source.fromPath(args(0)).parseCsv(keyExtractingClosure)

      println(keys)
  }
}