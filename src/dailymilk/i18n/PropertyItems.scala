package dailymilk.i18n

// Item types
object Scope extends Enumeration {
  type Scope = Value
  val AllLangs = Value("AllLangs")
  val SingleLang = Value("SingleLang")
}
import Scope._

trait Item {
  def scope = AllLangs
}
case class LabelItem(label: String, sc: Scope) extends Item {
  def this(label: String) = this(label, AllLangs)
  override def scope = sc
}
case class PropertyItem(key : String, value: String, sc: Scope) extends Item {
  override def scope = sc
}
case class EmptyItem() extends Item