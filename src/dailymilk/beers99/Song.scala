package dailymilk.beers99

object Song {
  import scala.actors._
  import scala.actors.Actor._

  abstract class Beverage {
    def name = this.toString.toLowerCase
  }
  case object Beer extends Beverage

  object Wall {
    private var contents : List[Beverage] = Nil

    def count(what: Beverage) = contents count (_ == what)
    def isEmpty = contents isEmpty
    def stock(n: Int, what: Beverage) = contents :::= List.make(n, what)
    def get(what: Beverage) {
      def takeOneFrom(contents: List[Beverage]) : List[Beverage] = contents match {
        case `what` :: rest => rest
        case other :: rest => other :: takeOneFrom(rest)
        case Nil => println("Sorry, we are out of " + what.name); Nil
      }
      contents = takeOneFrom(contents)
    }
  }

  sealed abstract class Messages
  case class SingSong(what: Beverage) extends Messages
  case class HowManyMore(what: Beverage) extends Messages
  case class HowManyNow(what: Beverage) extends Messages
  case class ThereAreStill(n: Int, what: Beverage) extends Messages
  case class ThereAreNow(n: Int, what: Beverage) extends Messages
  case class Gimme(what: Beverage) extends Messages
  case class HereIs(what: Beverage) extends Messages
  case class ClosingTime extends Messages

  def plural(count: Int, noun: String, nouns: String) = if (count == 1) noun else nouns
  def countIt(n: Int, what: Beverage) = "%d %s of %s" format(n, plural(n, "bottle", "bottles"), what.name)

  object Waitress extends Actor {
    def tellThem(what: String) = println("%s on the wall " format what)
    def act = loop {
      react {
        case HowManyMore(it) =>
          val total = Wall count it
          tellThem(countIt(total, it))
          reply(ThereAreStill(total, it))
        case Gimme(it) =>
          print("Take one down, ")
          Wall get it
          reply(HereIs(it))
        case HowManyNow(it) =>
          val total = Wall count it
          tellThem(countIt(total, it))
          if (Wall isEmpty) {
            reply(ClosingTime)
            exit
          } else {
            reply(ThereAreNow(total,it))
          }
        case _ =>
          println("You wish, honey!")
      }
    }
  }

  object Patrons extends Actor {
    def act = loop {
      react {
        case SingSong(what: Beverage) =>
          Waitress ! HowManyMore(what)
        case ThereAreStill(n, it) =>
          println(countIt(n, it))
          Waitress ! Gimme(it)
        case HereIs(it) =>
          println("pass it around")
          Waitress ! HowManyNow(it)
        case ThereAreNow(n, it) =>
          println()
          Waitress ! HowManyMore(it)
        case ClosingTime =>
          exit
        case _ =>
          println("Say what???")
      }
    }
  }

  def Sing99Beers = {
    Wall stock (99, Beer)
    Waitress.start
    Patrons.start

    Patrons ! SingSong(Beer)
  }

  def main(args: Array[String]) : Unit = {
    Sing99Beers
  }
}