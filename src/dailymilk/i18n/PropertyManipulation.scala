package dailymilk.i18n

import scala.util.parsing.combinator.syntactical._

abstract class PropertyOperation
case class RemovePropertyKey(key: String) extends PropertyOperation
case class ChangePropertyValue(key: String) extends PropertyOperation
case class MovePropertyKeyToAnother(oldKey: String, newKey: String) extends PropertyOperation
case class CreateSection(key: String, label: String, hidden: Boolean) extends PropertyOperation

object PropertiesManipulationDsl extends StandardTokenParsers {

  lexical.delimiters ++= List("(", ")", ";", ".")
  lexical.reserved += ("rm", "remove", "mv", "move", "chng", "change", "to", "section", "sec", "hide")

  private def instr : Parser[List[PropertyOperation]] = repsep(command, ";") ^^ { case l => l }

  private def command : Parser[PropertyOperation] = (operation ~ properties | section ~ stringLit ~ identifier) ^^ {
    case (op : String) ~ (props : (String, String)) => createOperation(op, props)
    case (sectionKind : String) ~ (label : String) ~ (key: String) => createSection(sectionKind, key, label)
  }

  private def operation = ("rm" | "remove" | "mv" | "move" | "chng" | "change") ^^ {
    case "remove" => "rm"
    case "move" => "mv"
    case "change" => "chng"
    case s => s
  }

  private def properties : Parser[(String, String)] = (identifier ~ "to" ~ identifier | identifier) ^^ {
      case (newProp : String) ~ "to" ~ (oldProp : String) => (newProp, oldProp)
      case justProp : String => (justProp, null)
  }

  private def identifier : Parser[String] = repsep(ident, ".") ^^ { case idents: List[String] => idents.mkString(".") }

  private def section = ("sec" | "section" | "hide" ) ^^ {
    case "section" => "sec"
    case s => s
  }

  private def createOperation(op: String, props: (String, String)) : PropertyOperation = op match {
    case "rm" => RemovePropertyKey(props._1)
    case "mv" => MovePropertyKeyToAnother(props._1, props._2)
    case "chng" => ChangePropertyValue(props._1)
  }

  private def createSection(kind: String, key: String, label: String) : PropertyOperation = kind match {
    case "hide" => CreateSection(key, label, true)
    case "sec" => CreateSection(key, label, false)
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
}