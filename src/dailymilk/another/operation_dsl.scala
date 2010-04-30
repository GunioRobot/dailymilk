import scala.util.parsing.combinator.syntactical._

abstract class Operation

case class RemoveResourceKey(key: String) extends Operation

case class ChangeResourceValue(key: String) extends Operation

case class MoveResourceKeyToAnother(oldKey: String, newKey: String) extends Operation

case class CreateSection(key: String, label: String, hidden: Boolean) extends Operation

object ResourceOperationDsl extends StandardTokenParsers {

  lexical.delimiters ++= List("(", ")", ";", ".")
  lexical.reserved += ("rm", "remove", "mv", "move", "chng", "change", "to", "section", "sec", "hide")

  private def instr : Parser[List[Operation]] = repsep(command, ";") ^^ { case l => l }

  private def command : Parser[Operation] = (operation ~ properties | section ~ stringLit ~ identifier) ^^ {
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

  private def createOperation(op: String, props: (String, String)) : Operation = op match {
    case "rm" => RemoveResourceKey(props._1)
    case "mv" => MoveResourceKeyToAnother(props._1, props._2)
    case "chng" => ChangeResourceValue(props._1)
  }

  private def createSection(kind: String, key: String, label: String) : Operation = kind match {
    case "hide" => CreateSection(key, label, true)
    case "sec" => CreateSection(key, label, false)
  }

  def parse(dsl : String) : List[Operation] = {
    val tokens = instr(new lexical.Scanner(dsl)) match {
      case Success(op, _) => op
      case Failure(msg, _) => { println(msg); Nil }
      case Error(msg, _) => { println(msg); Nil }
    }
    tokens
  }

  def parseSingle(dsl: String) : Operation = {
    val token = command(new lexical.Scanner(dsl)) match {
      case Success(op, _) => op
      case Failure(msg, _) => { println(msg); null }
      case Error(msg, _) => { println(msg); null }
    }
    token
  }
}

object ResourceOperationDslTest {

  val key = "test.key"

  val oldKey = "test.old.key"

  val validSequence = Map(
      List("rm", key) -> RemoveResourceKey(key),
      List("remove", key) -> RemoveResourceKey(key),
      List("chng", key) -> ChangeResourceValue(key),
      List("change", key) -> ChangeResourceValue(key),
      List("mv", oldKey, "to", key) -> MoveResourceKeyToAnother(oldKey, key),
      List("move", oldKey, "to", key) -> MoveResourceKeyToAnother(oldKey, key)
    )

  def main(args: Array[String]) {
    testValidSequences
  }

  def testValidSequences = {
    for (seq <- validSequence) {
      val (partsToCombine, expectedResult) = seq
      val stringyCommand = partsToCombine.mkString(" ")
      val result = ResourceOperationDsl.parseSingle(stringyCommand)
      assert(expectedResult == result,
        "Found: " + result + ", expected was: " + expectedResult + " based on \"" + stringyCommand + "\"")
    }
  }
}