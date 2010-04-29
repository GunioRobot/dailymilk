package dailymilk.ruminations

import java.util.Date

/**
 * Example from "Ruminations of a Programmer"
 * Article named: "Dependency Injection as Function Currying"
 */

trait CreditService

trait AuthService

case class RealPayment(creditService: CreditService, authService: AuthService, startDate: Date, amount: Int)

case class PayPal(provider: String) extends CreditService

case class DefaultAuth(provider: String) extends AuthService

object RealPaymentApp {
  def main(args: Array[String]) {
    val paypalPayment = RealPayment(PayPal("bar"), _: AuthService, _: Date, _: Int)
    println(paypalPayment)

    val z = paypalPayment(DefaultAuth("foo"), new Date(), 100)
    println(z)

    val paypalPaymentCurryied = Function.curried(paypalPayment)
    println(paypalPaymentCurryied)

    val curried = paypalPaymentCurryied(DefaultAuth("foo"))
    println(curried)

    val zz = curried(new Date())(150)
    println(zz)
  }
}
