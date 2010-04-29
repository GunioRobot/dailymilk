package dailymilk.ruminations

/**
 * Example from "Ruminations of a Programmer"
 * Article named: "DSL Composition techniques in Scala"
 */

trait SalaryProcessing {
  // abstract type
  type Salary

  // type synonym
  type Tax = (Int, Int)

  // basic domain operations
  def basic: BigDecimal
  def allowances: BigDecimal
  def tax: Tax
  def net(s: String): Salary
}

trait SalaryComputation extends SalaryProcessing {
  type Salary = BigDecimal

  def basic = 34
  def allowances = 23
  def tax = (2, 4)

  private def factor(s: String) = {
    5
  }

  def net(s: String) = {
    val (t1, t2) = tax
    basic + allowances - (t1 + t2 * factor(s))
  }
}

object salary extends SalaryComputation

trait Accounting extends SalaryProcessing {
  // abstract value
  val semantics: SalaryProcessing
  // define type to use same semantics
  type Salary = (semantics.Salary, semantics.Tax)

  def basic = semantics.basic
  def allowances = semantics.allowances
  def tax = semantics.tax
  def net(s: String) = {
    (semantics.net(s), tax)
  }
}

object acc extends Accounting {
  val semantics = salary
}

object SalaryAccountingApp {
  /*def pay(semantics: SalaryProcessing, employees: List[String]) : List[semantics.Salary] = {
    //import semantics._
    employees map (semantics.net _)
  } */

  def main(args: Array[String]) {
    val employees = List("Bobor", "Hurka", "Sano", "Skaja")
    //println(pay(salary, employees))
   // println(pay(acc, employees))
  }
}