class X(val i : Int) { def add(implicit x: X) = println(x.i + i) }

object X {
  implicit def xx = new X(3)
}

new X(3).add

val other = new {
  def print(implicit x: X) = println(x.i)
}

other.print

implicit def x = new X(32)

other.print

case class A(i: Int)
object A { implicit def int2A(i: Int) = new A(i) }
val a: A = 2
println(a)