package dailymilk.logging

import util.DynamicVariable

/**
 * Basic timing functionality
 */

trait Timing {

  implicit def string2TimingLabel(label: String) = new TimingLabel(label)

  class TimingLabel(val label: String) {
    def duration[T](f: => T): T = new MeasuringContext(Timing.this, label, f).result
    def measure[T](f: => T): MeasuringContext[T] = new MeasuringContext(Timing.this, label, f)
  }

  lazy val metricScope = new DynamicVariable(new DurationMetric)
  lazy val labelScope = new DynamicVariable(this.getClass.toString)
  def currentSpent = metricScope.value.spent
  def at = labelScope.value.label

  def duration[T](f: => T): T = new MeasuringContext(this, autoLabel, f).result
  def measure[T](f: => T): MeasuringContext[T] = new MeasuringContext(this, autoLabel, f)

  def resolveLog: Logging = {
    if (this.isInstanceOf[Logging]) this.asInstanceOf[Logging] else defaultLog
  }
  // TODO Lookup pre commonLog cez DI
  protected def defaultLog = Logging.commonLog
  protected def autoLabel: String = Timing.nextLabel
}

class MeasuringContext[T](val t: Timing, val label: String, f: => T) {
  private val log = t.resolveLog

  private lazy val spent_ = t.metricScope.value.spent
  private lazy val start_ = t.metricScope.value.start
  private lazy val result_ = f

  t.labelScope.withValue(label) {
    t.metricScope.withValue(new DurationMetric) {
      val result = try {
        val start = start_
        // TODO pouzitie formatera pre start message a start cas
        log.info("Start of: " + label)
        result_
      } finally {
        val spent = spent_
        // TODO pouzitie formatera pre end message a end cas
        log.info("End of: " + label + " [spent: " + spent_ + "ms]");
      }
    }
  }
  def start = start_
  def spent = spent_
  def result = result_
}

object Timing {
  var count = 0
  // TODO chyba tu synchronizacia pre increment count-u
  def nextLabel = {
    count += 1
    // TODO pouzitie formatera cez DI pre measure label
    "measure" + count
  }
}

class DurationMetric {
  val start = System.currentTimeMillis
  def spent = System.currentTimeMillis - start
}