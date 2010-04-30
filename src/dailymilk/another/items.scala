
abstract class Item(lineNo: Int, key: String) {
  require(lineNo >= 0, "Line no can not be negative")
  require(key != null && key.trim.nonEmpty, "Key can not be empty")

  def keysByPriority = {
    val parts = key.split('.').toList;
    composeKeys(parts)
  }

  private def composeKeys(parts : List[String]) : List[String] = {
    if (parts.nonEmpty) parts.mkString(".") :: composeKeys(parts.init)
    else Nil
  }
}

case class SectionItem(lineNo: Int, key: String, name: String) extends Item(lineNo, key) {
  require(name != null && name.trim.nonEmpty, "Name can not be empty")
}

case class ResourceItem(lineNo: Int, key: String) extends Item(lineNo, key) {
  import scala.collection.mutable.{Map => MutableMap}

  private val _values: MutableMap[String, String] = MutableMap[String, String]()

  def values = Map[String, String]() ++ _values

  def addValue(lang: String, value: String) {
    _values += (lang -> value)
  }

}

// TODO write as ScalaTest
object ItemsTest {

  val key = "test.key.chain"

  val name = "Dummy"

  def main(args: Array[String]) {
    testKeyPrioritisation
    testArgumentValidity
  }

  def testArgumentValidity = {
    // Resource item
    itemIllegalArgument(createResourceItem(-1, key))
    itemIllegalArgument(createResourceItem(0, ""))
    itemIllegalArgument(createResourceItem(0, " "))
    itemIllegalArgument(createResourceItem(0, "\n"))
    itemIllegalArgument(createResourceItem(0, null))
    // Section item
    itemIllegalArgument(createSectionItem(-1, key, name))
    itemIllegalArgument(createSectionItem(0, "", name))
    itemIllegalArgument(createSectionItem(0, " ", name))
    itemIllegalArgument(createSectionItem(0, "\n", name))
    itemIllegalArgument(createSectionItem(0, null, name))
    itemIllegalArgument(createSectionItem(0, key, ""))
    itemIllegalArgument(createSectionItem(0, key, " "))
    itemIllegalArgument(createSectionItem(0, key, "\n"))
    itemIllegalArgument(createSectionItem(0, key, null))
  }

  private def createResourceItem(lineNo: Int, key: String) = (() => { ResourceItem(lineNo, key) }, (lineNo, key))
  private def createSectionItem(lineNo: Int, key: String, name: String) = (() => { SectionItem(lineNo, key, name) }, (lineNo, key, name))

  private def itemIllegalArgument(tuple : (() => Item, Any)) = {
    import scala.util.control.Exception._

    val (function, args) = tuple
    val illegalArgument = catching(classOf[IllegalArgumentException])
    val result = illegalArgument.opt {
      function()
    }
    assert(result == None, "Invariants for arguments are broken, with combination: " + args)
  }

  def testKeyPrioritisation = {
    checkKeyPrioritisation(ResourceItem(0, key))
    checkKeyPrioritisation(SectionItem(0, key, name))
  }

  private def checkKeyPrioritisation(item : Item) = {
    val item = ResourceItem(0, key)

    val keyParts = key.split('.').toList
    val prioritizedKeys = item.keysByPriority

    assert(prioritizedKeys.contains(key), prioritizedKeys + " does not contain root key " + key)
    assert(prioritizedKeys.size == keyParts.size, "no enough keys, expected: " + keyParts.size + " found: " + prioritizedKeys.size)
    (key /: prioritizedKeys) { (curKey: String, prevKey: String) =>
      assert(curKey.contains(prevKey), "Wrong ordered, item: " + curKey + " does not contain: " + prevKey)
      prevKey
    }
  }

}
