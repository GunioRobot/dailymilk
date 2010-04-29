package dailymilk.i18n

import io.Source

/**
 * Simple CSV parser which parser everything into memory
 * Configurable properties are: separators and startLineNo
 */

class CsvParser(startLineNo: Int, separators: Char*) {

  def parse(src: Source, closure: (Int, Array[String]) => Unit) = {
    val lines = src.getLines()
    (startLineNo /: lines) { (lineNo : Int, line: String) => {
        closure(lineNo, line.split(separators.toArray))
        lineNo + 1
      }
    }
  }
}

object CsvParser {

  val startLineNo = 1
  val separators : Array[Char] = Array(';')

  implicit def source2CsvParser(src: Source) = new CsvParserSourceWrapper(src, new CsvParser(startLineNo, separators: _*))

  
}

class CsvParserSourceWrapper(val src: Source, val parser: CsvParser) {
  def parseCsv(closure: (Int, Array[String]) => Unit) = {
    parser.parse(src, closure)
  }
}