package dailymilk.ruminations

/**
 * Example from "Ruminations of a Programmer"
 * Article named: A Case for Orthogonality in Design
 */

case class Address(no: Int, street: String, city: String, country: String, zip: String)

trait LabelMaker {
  def toLabel: String
}

object Address {
  implicit def AddressToLabelMaker(a: Address) = new LabelMaker {
    override def toLabel = {
      println("implicit overriden was called")
      "%d-%s, %s, %s-%s" format (a.no, a.street, a.city, a.country, a.zip)
    }
  }

  def main(args: Array[String]) {
    val a = new Address(2, "Sustekova", "Bratislava", "Slovensko", "851 04") with LabelMaker {
      override def toLabel = {
        println("a overriden was called")
        "%d-%s, %s, %s-%s" format (no, street, city, country, zip)
      }
    }
    println(a toLabel)
    
    val b = new Address(13, "Kleeblatt gasse", "Wien", "Oestereich", "A-1010")
    println(b toLabel)
  }
}

