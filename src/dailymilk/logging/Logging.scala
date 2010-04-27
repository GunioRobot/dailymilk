package dailymilk.logging

import org.slf4j.LoggerFactory

/**
 * Basic logging functionality
 */

trait Logging  {
  private val log = LoggerFactory.getLogger(getClass);

  def trace(message: String, values: Any*) =
    log.trace(message, values.map(_.asInstanceOf[Object]).toArray)
  def trace(message: String, cause: Throwable) =
    log.trace(message, cause)
  def trace(message: String, cause: Throwable, values: Any*) =
    log.trace(message, cause, values.map(_.asInstanceOf[Object]).toArray)

  def debug(message: String, values: Any*) =
    log.debug(message, values.map(_.asInstanceOf[Object]).toArray)
  def debug(message: String, cause: Throwable) =
    log.debug(message, cause)
  def debug(message: String, cause: Throwable, values: Any*) =
    log.debug(message, cause, values.map(_.asInstanceOf[Object]).toArray)

  def info(message: String, values: Any*) =
    log.info(message, values.map(_.asInstanceOf[Object]).toArray)
  def info(message: String, cause: Throwable) =
    log.info(message, cause)
  def info(message: String, cause: Throwable, values: Any*) =
    log.info(message, cause, values.map(_.asInstanceOf[Object]).toArray)

  def warn(message: String, values: Any*) =
    log.warn(message, values.map(_.asInstanceOf[Object]).toArray)
  def warn(message: String, cause: Throwable) =
    log.warn(message, cause)
  def warn(message: String, cause: Throwable, values: Any*) =
    log.warn(message, cause, values.map(_.asInstanceOf[Object]).toArray)

  def error(message: String, values: Any*) =
    log.error(message, values.map(_.asInstanceOf[Object]).toArray)
  def error(message: String, cause: Throwable) =
    log.error(message, cause)
  def error(message: String, cause: Throwable, values: Any*) =
    log.error(message, cause, values.map(_.asInstanceOf[Object]).toArray)

}

object Logging {
  def commonLog = {
    class StdOutLogging extends Logging {
      override def info(message: String, values: Any*) = {
        println(message)
      }
    }
    new StdOutLogging
  }
}