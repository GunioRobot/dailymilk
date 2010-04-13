package dailymilk.gridcard

import scala.collection.mutable.{Map => MutMap}
import scala.util.Random

// TODO think about functional style and mutability
// TODO build as module, try to design SPI and API 
class GridCard(private val content: Map[(Char, Int), String]) {
  private val keys = content.keySet.toArray

  def combination(position: (Char, Int)) = content(position)
  // TODO implement strategy to no allow pick same combination in subsequent calls
  def pick = keys(Random.nextInt(keys.size))
  def check(position: (Char, Int), code: String) = content(position) == code
}

object GridCard {
  
  private val alphabet = 'a' to 'z' toArray
  private val digits = '0' to '9' toArray

  private val alphanumeric = alphabet ++ digits

  def apply(rows: Int, cols: Int, codeSize: Int) = {

    def generateCodes(it: Int, size: Int, cache: List[String]): List[String] = {
      if (it == 0) cache
      else {
        val code = generateSingleCode(size)
        // TODO implement strategy for variabile checks
        if (code.exists(alphabet.contains(_)) && code.exists(digits.contains(_)) && !cache.contains(code)) {
          generateCodes(it - 1, size, (code mkString) :: cache)
        } else generateCodes(it, size, cache)
      }
    }

    def generateSingleCode(size: Int) = (0 until size toList).map { i =>
      // TODO implement stratgy for variabile content sets, e.g. only digits, alphanumerical ...
      alphanumeric(Random.nextInt(alphanumeric.size))
    }

    val codes = generateCodes(rows * cols, codeSize, Nil)

    var content = MutMap[(Char, Int), String]()
    for (i <- 0 until rows; j <- 0 until cols) {
      content((alphabet(i), j)) = codes(i * j)
    }
    new GridCard(Map() ++ content)
  }
}