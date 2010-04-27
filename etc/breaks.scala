import scala.util.control._

def loop(f : Int => Boolean) = {
  val Inner = new Breaks
  Inner.breakable {
    for (i <- 1 to 4) if (f(i)) Inner.break else println(i)
  }
}

println("handling Outer")
val Outer = new Breaks
Outer.breakable {
  while(true) {
    loop { i => if ( i == 4) Outer.break; false }
  }
}

{
  import scala.util.control.Breaks._

  println("handling unnamed")
  breakable {
    while(true) {
      loop { i => if ( i == 4) break; false }
    }
  }
}

println("this is the end")


Breaks.breakable {
  for (i <- 1 to 5) {
    try {
      println(i)
      if  (i > 2) Breaks.break
    } catch { case e => println("break caught") }
  }
}