package dailymilk.ruminations

/**
 * Created by IntelliJ IDEA.
 * User: teliatko
 * Date: Apr 23, 2010
 * Time: 11:57:00 PM
 * To change this template use File | Settings | File Templates.
 */

trait X {
  println("creating x")
  val x : java.io.File
}
trait Y { this : X =>
  println("creating y")
  lazy val y = x.getName
  def z = x.getAbsolutePath
}
trait Z extends X {
  println("creating y")
  lazy val y = x.getName
  def z = x.getAbsolutePath
}

object TraitCreationApp {
  def main(args : Array[String]) {
    new Y with X {
      println("creating concrete")
      val x = new java.io.File("hi")
    }

    new Z {
      println("creating concrete")
      val x = new java.io.File("hi")
    }
  }
}