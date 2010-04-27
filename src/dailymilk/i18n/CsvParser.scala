package dailymilk.i18n

import io.Source

/**
 * Simple CSV parser which parser everything into memory
 * Configurable properties are: separators and startLineNo
 */

class CsvParser(startLineNo: Int, separators: Char*) {

  def parse(src: Source, closure: (Int, Array[String]) => Option[String]) : List[String] = {
    val lines = src.getLines()
    val startTuple = (startLineNo, List[String]())
    val endResult = (startTuple /: lines) { (tuple: Tuple2[Int, List[String]], line: String) => {
        val (lineNo, results) = tuple
        closure(lineNo, line.split(separators.toArray)) match {
          case Some(s: String) => (lineNo + 1, s :: results)
          case None => (lineNo + 1, results)
        }
      }
    }
    endResult._2
  }
}

object CsvParser {

  val startLineNo = 1
  val separators : Array[Char] = Array(';')

  implicit def source2CsvParser(src: Source) = new CsvParserSourceWrapper(src, new CsvParser(startLineNo, separators: _*))

  
}

class CsvParserSourceWrapper(val src: Source, val parser: CsvParser) {
  def parseCsv(closure: (Int, Array[String]) => Option[String]) = {
    parser.parse(src, closure)
  }
}