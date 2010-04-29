package dailymilk.ruminations

/**
 * Example from "Ruminations of a Programmer"
 * Article named: "Scala Self-Type Annotations for Constrained Orthogonality"
 */

case class Trade(refNo: String, account: String, instrument: String, quantity: Int, unitPrice: Int) {
  def principal = quantity * unitPrice
}

trait Tax { this: Trade =>
  def calculateTax = principal * 0.2
}

trait Commission { this: Trade =>
  def calculateCommission = principal * 0.15
}

object TradeApp {
  def main(args: Array[String]) {
    val v = new Trade("1", "1", "override", 23, 2) with Tax with Commission {
      override def calculateTax = (principal * 0.2f).asInstanceOf[Int]
      override def calculateCommission = (principal * 0.15f).asInstanceOf[Int]
    }
    println(v + " with tax: " + v.calculateTax + ", commission: " + v.calculateCommission);

    val t = new Trade("1", "1", "default", 23, 2) with Tax with Commission
    println(t + " with tax: " + t.calculateTax + ", commission: " + t.calculateCommission);
  }
}