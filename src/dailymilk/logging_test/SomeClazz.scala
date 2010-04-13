package dailymilk.logging

class SomeClazz extends Logging with Timing {
  trace("Instance created at {}", new java.util.Date)

  def nothing = {
    val interval = 4000

    val x = "algorithm" duration {
      val res = algorithm(interval)
      info("Spent at: {} was: {}", at, currentSpent)
      // TODO pouzitie measure vo vnutri merania
      val y = "algorithm2" measure {
        val res = algorithm(interval)
        info("Spent at: {} was: {}", at, currentSpent)
        res
      }
      info("result: {}, start: {}, spent: {}", y.result, y.start, y.spent)
      // TODO custom formatovanie start, spent
      res
    }
    info("x = {} [{}]", x, currentSpent)

    val z = measure {
      algorithm(interval)
      info("Spent at: {} was: {}", at, currentSpent)
    }
    info("start: {} [{}]", z.start, currentSpent)
    info("at: {}", at)
  }

  def algorithm(interval : Int) = {
    Thread.sleep(interval)
    interval + "ms"
  }

}

object SomeClazz {
  def main(args: Array[String]) {
    val a = new SomeClazz
    a.nothing
  }
}