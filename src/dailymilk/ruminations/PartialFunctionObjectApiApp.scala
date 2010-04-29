package dailymilk.ruminations

/**
 * Example from "Daily Scala"
 * Article named: "Methods of PartialFunction object"
 */

import PartialFunction._

object IntNum {

  import util.control.Exception._

  val number = catching(classOf[NumberFormatException])
  def unapply(x : Any) = condOpt(x) {
    case x : Int => x
    case y : String if number.opt(y.toInt).isDefined => y.toInt
  }
}

object IntNumBetter {

  import util.control.Exception._

  val number = catching(classOf[NumberFormatException])
  def unapply(x : Any) = x match {
    case x : Int => Some(x)
    case y : String => number.opt(y.toInt)
    case _ => None
  }
}

object PartialFunctionObjectApiApp {
  
  def strangeConditional(other: Any): Boolean = cond(other) {
    case x: String if x == "abc" || x == "def" => true
    case x: Int => true
  }

  def onlyInt(v: Any) : Option[Int] = condOpt(v) { case x: Int => x }

  def main(args: Array[String]) {
    println(strangeConditional("abc"))
    println(strangeConditional("hello"))
    println(strangeConditional(2))

    println(onlyInt("oops"))
    println(onlyInt(34))

    1 match { case IntNum(x) => println(x + " matched") }
    "2" match { case IntNum(x) => println(x + " matched") }
    "hola" match {
      case IntNum(x) => println(x + " matched")
      case _ => println("not matched")
    }

    1 match { case IntNumBetter(x) => println(x + " matched") }
    "2" match { case IntNumBetter(x) => println(x + " matched") }
    "hola" match {
        case IntNumBetter(x) => println(x + " matched")
        case _ => println("not matched")
    }
  }
}