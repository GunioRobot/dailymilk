package dailymilk.ruminations

/**
 * Example from "Daily Scala"
 * Article named: "Methods on PartialFunction"
 */

object PartialFunctionApiApp {

  def run(f : Function[Any, Unit]) = println(f.isInstanceOf[PartialFunction[_,_]])

  def run1(f : PartialFunction[Any, Unit]) = println(f.isInstanceOf[PartialFunction[_,_]])

  def run2(f : Function2[Int, String, Unit]) = f(1, "2")

  def main(args: Array[String]) {
    type PF = PartialFunction[Int, Int]

    val pf1 : PF = { case 1 => 2 }
    val pf2 : PF = { case 2 => 3 }

    println(pf1 isDefinedAt 1)
    println(pf2 isDefinedAt 2)

    // pf1(2) // throws MatchError
    println(pf1(1))

    println((pf1 andThen pf2) isDefinedAt 2)
    println((pf1 andThen pf2) isDefinedAt 1)

    // (pf1 andThen pf2)(2) // throws MatchError
    println((pf1 andThen pf2)(1))

    println((pf1 orElse pf2) isDefinedAt 2)
    println((pf1 orElse pf2) isDefinedAt 1)
    println((pf1 orElse pf2) isDefinedAt 3)

    println((pf1 orElse pf2)(2))
    println((pf1 orElse pf2)(1))

    println(pf1 lift 1)
    println(pf1 lift 2)

    run({ case f => () })
    run1({ case f => () })
    run2({
      case (1, b) => println(b)
      case (a, b) => println((a, b))
    })
  }
}