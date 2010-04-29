package dailymilk.ruminations

/**
 * Defines DSL for managing property file
 */

import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._
import io.Source
import util.Properties
import java.util.{Properties, ResourceBundle}

abstract class PropertyOperation
case class RemovePropertyKey(key: String) extends PropertyOperation
case class ChangePropertyValue(key: String) extends PropertyOperation
case class MovePropertyKeyToAnother(oldKey: String, newKey: String) extends PropertyOperation

object PropertiesManipulationDsl extends StandardTokenParsers {

  lexical.delimiters ++= List("(", ")", ";", ".")
  lexical.reserved += ("rm", "remove", "mv", "move", "chng", "change", "to")

  private def instr : Parser[List[PropertyOperation]] = repsep(command, ";") ^^ { case l => l }

  private def command : Parser[PropertyOperation] = operation ~ properties ^^ { case op ~ props => createOperation(op, props) }

  private def operation = ("rm" | "remove" | "mv" | "move" | "chng" | "change") ^^ {
    case "remove" => "rm"
    case "move" => "mv"
    case "change" => "chng"
    case s => s
  }

  private def properties : Parser[(String, String)] = (repsep(ident, ".") ~ "to" ~ repsep(ident, ".") | repsep(ident, ".")) ^^ {
      case (newProp : List[String]) ~ "to" ~ (oldProp : List[String]) => (newProp.mkString("."), oldProp.mkString("."))
      case justProp : List[String] => (justProp.mkString("."), null)
  }

  private def createOperation(op: String, props: (String, String)) : PropertyOperation = op match {
    case "rm" => RemovePropertyKey(props._1)
    case "mv" => MovePropertyKeyToAnother(props._1, props._2)
    case "chng" => ChangePropertyValue(props._1)
  }

  def parse(dsl : String) : List[PropertyOperation] = {
    val tokens = instr(new lexical.Scanner(dsl)) match {
      case Success(op, _) => op
      case Failure(msg, _) => { println(msg); Nil }
      case Error(msg, _) => { println(msg); Nil }
    }
    tokens
  }

  def parseSingle(dsl: String) : PropertyOperation = {
    val token = command(new lexical.Scanner(dsl)) match {
      case Success(op, _) => op
      case Failure(msg, _) => { println(msg); null }
      case Error(msg, _) => { println(msg); null }
    }
    token
  }

  def main(args: Array[String]) {
    val dslExample = "mv old.key to new.key; rm another.key; remove jet.another.key; move; change this.key"
    println(parse(dslExample))
  }
  
}

object PropertiesDslApp {
  def main(args: Array[String]) {
    val lines = Source.fromInputStream(getClass.getResourceAsStream("example.properties")).getLines().toList
    val commands = for (line <- lines if line.startsWith("#!"))
                   yield PropertiesManipulationDsl.parseSingle(line.drop(2).trim)
    println(commands)

    val props = new Properties()
    props.load(getClass.getResourceAsStream("example.properties"))
    println(props.getProperty("xrail.label.relation"))

    for (command <- commands) {
      command match {
        case RemovePropertyKey(key) if props.containsKey(key)  => props.remove(key)
        case MovePropertyKeyToAnother(oldKey, newKey) if props.containsKey(oldKey) => {
          if (props.containsKey(oldKey)) {
            props.put(newKey, props.getProperty(oldKey))
            props.remove(oldKey)
          }
        }
        case _ => println(command + "not applied")
      }
    }
    props.list(Console.out)
  }
}
