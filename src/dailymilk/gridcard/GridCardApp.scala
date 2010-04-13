package dailymilk.gridcard

/**
 * Created by IntelliJ IDEA.
 * User: mbigos
 * Date: 11.03.2010
 * Time: 09:18:41
 * To change this template use File | Settings | File Templates.
 */

// TODO write as unit test
// TODO write also benchmark
object GridCardApp {
  def main(args: Array[String]) {
    val gc = GridCard(5, 8, 4)

    for (i <- 0 to 8) {
      val key = gc.pick
      val code = gc.combination(key)
      println(key + ": " + code + " is " + gc.check(key, code))
    }
  }
}