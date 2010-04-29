package dailymilk.ruminations

/**
 * Inspired by example from "Ruminations of a Programmer"
 * Article named: "Generic Repository and DDD - Revisited"
 */

// Domain repository, defines basic methods used for specific domain classes
trait Repository[T] {

  val repositoryImpl: RepositoryImpl

  protected def find(query: String, params: Array[Object]) : List[T] = {
    repositoryImpl.find(query, params)
  }
}

// General common methods used by domain repositories
trait RepositoryImpl {
  def find[T](query: String, params: Array[Object]) : List[T]
}

// Concrete implementation e.g for JPA, Hibernate or JDBC ...
object ConcreteRepositoryImpl extends RepositoryImpl {
  def find[T](query: String, params: Array[Object]) : List[T] = {
    println("concrete repository find with: '" + query + "' params: " + params.mkString("[", ",", "]"))
    List()
  }
}

// EXAMPLE #1:
// Example of domain object
case class Dummy(foo: String, bar: String)

// Concrete domain object repository.
// Exposes only specific methods for concrete domain object and its use cases
trait DummyRepository extends Repository[Dummy] {
  def findDummiesByIq(iq : Int) = {
    find("from Dummy d where d.iq <= :iq", Array(java.lang.Integer.valueOf(iq)))
  }
}

// Singleton to access repository of Dummy domain iobject
object DummyRep extends DummyRepository {
  val repositoryImpl = ConcreteRepositoryImpl
}

// EXAMPLE #2:
// Example of another domain object
case class Dummy2(foo: String, bar: String)

// Concrete domain object repository
// Directly object which defines its underlaying implementation
object Dummy2Rep extends Repository[Dummy2] {
  val repositoryImpl = ConcreteRepositoryImpl

  def findDummiesByIq(iq : Int) = {
    find("from Dummy2 d where d.iq <= :iq", Array(java.lang.Integer.valueOf(iq)))
  }
}

// EXAMPLE #3:
// Example 1 can be used to compose use case repositories

case class Dummy3(foo: String, bar: String)

trait Dummy3Repository extends Repository[Dummy3] {
  def findDummiesByIq(iq : Int) = {
    find("from Dummy d where d.iq = :iq", Array(java.lang.Integer.valueOf(iq)))
  }
}

//object UseCaseWithDummiesRep extends DummyRepository with Dummy3Repository {
//  val repositoryImpl = ConcreteRepositoryImpl
//}

object RepositoryApp {
  def main(args: Array[String]) {
    DummyRep.findDummiesByIq(75)
    Dummy2Rep.findDummiesByIq(85) // little bit more inteligent
    //UseCaseWithDummiesRep.findDummiesByIq(78)
    //UseCaseWithDummiesRep.findDummiesByIq(100)
  }
}