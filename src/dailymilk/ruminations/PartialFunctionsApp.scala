package dailymilk.ruminations

import scala.PartialFunction

/**
 * Example from "Daily Scala"
 * Article named: "Chaining Partial Functions with orElse"
 */

object PartialFunctionsApp {
  def main(args: Array[String]) {
    val i : PartialFunction[Any, Unit] = { case x: Int => println("int found") }
    val d : PartialFunction[Any, Unit] = { case x: Double => println("double found") }
    val * : PartialFunction[Any, Unit] = { case x => println("something else found") }

    (i orElse d orElse *)(1)
    (i orElse d orElse *)(1.6)
    (i orElse d orElse *)("foo")

    type =>?[-A, +B] = PartialFunction[A, B]
    val o : Any =>? Unit = {case x:Int => println("int found")}
    (o orElse d orElse *)(1)

    def =>?[A, B](id : A =>? B) = id
    (=>?[Any, Unit]{case s : String => println("String found")} orElse =>?[Any, Unit]{case i : Int => println("Int found")})("Hi")
  }
}