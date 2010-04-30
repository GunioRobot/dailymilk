package dailymilk.ruminations

/**
 * Defines DSL for managing property file
 */

import scala.util.parsing.combinator._
import io.Source
import util.Properties
import tools.nsc.io.File
import scala.collection.JavaConversions._
import java.util.{Date, Properties, ResourceBundle}
import dailymilk.i18n._

trait PropertyCollector {
  def addSection(item: LabelItem)
  def addProperty(item: PropertyItem)
}

trait DefaultPropertyCollector extends PropertyCollector {
  import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}
  import Scope._

  private val default = LabelItem("default", AllLangs)
  
  private val sections = MutableMap[LabelItem, MutableSet[PropertyItem]](default -> createBlankSet)

  def addSection(item : LabelItem) {
    if (!sections.contains(item)) {
      sections += (item -> createBlankSet)
    }
  }

  private def createBlankSet = MutableSet[PropertyItem]()

  def addProperty(item: PropertyItem) {
    containing(item.key) match {
      case Some(key) => sections(key) + item
      case None => sections(default) + item  
    }
  }

  private def containing(key: String) = {
    val keyParts = key.split(".").toList

    sections.keySet.find({ case LabelItem(key, _) => true })
  }
}

object PropertiesDslApp {

  def main(args: Array[String]) {
    if (args.length == 0 || args(0) == null || args(1) == null) {
      println("usage: scala PropertiesDslApp <property_file> <output_property_file>")
    }
    val file = File(args(0))

    // Strip commands
    val lines = Source.fromInputStream(file.bufferedInput).getLines().toList
    val commands = for (line <- lines if line.startsWith("#!"))
                   yield PropertiesManipulationDsl.parseSingle(line.drop(2).trim)
    // Load properties
    val props = new Properties()
    props.load(file.bufferedInput)

    // Apply commands
    for (command <- commands) {
      command match {
        case RemovePropertyKey(key) if props.containsKey(key)  => props.remove(key)
        case MovePropertyKeyToAnother(oldKey, newKey) if props.containsKey(oldKey) => {
          if (props.containsKey(oldKey)) {
            props.put(newKey, props.getProperty(oldKey))
            props.remove(oldKey)
          }
        }
        case _ => println("not applied command: " + command)
      }
    }

    val outFile = File(args(1))
    props.store(outFile.bufferedOutput(), "created at " + new Date)
  }
}
