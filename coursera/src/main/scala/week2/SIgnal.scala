package week2

import scala.util.DynamicVariable

/**
  * @author liww.li
  */
class StackVariable[T](init: List[T]) {
  private var values = init
  def value = values.head
  def withValue(newValue: T)(op: => T): T = {
    values = newValue :: values
    try op finally values = values.init
  }
}

class Signal[T](t: => T) {
  import Signal._
  private var myValue: T = _
  private var myExpr: () => T = _
  private var observers: Set[Signal[_]] = Set()
  private var observed: List[Signal[T]] = _
  update(t)

  def update(exp: => T): Unit = {
    myExpr = () => exp
    computeValue()
  }

  def computeValue(): Unit = {
    for(observer <- observed)
      observer.observers -= this
    observed = Nil
    val newValue = caller.withValue(this)(myExpr())
    myValue = newValue
    val obs = observers
    observers = Set()
    obs.foreach(_.computeValue())
  }

  def apply(): T = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic defined")
    //caller.value.observed ::= this
    myValue
  }
}

object Signal {
  val caller = new DynamicVariable[Signal[_]](NoSignal)
  def apply[T](expr: => T) = new Signal(expr)
}

object NoSignal extends Signal[Nothing](???) {
  override def computeValue() = {}
}

class Var[T](expr: => T) extends Signal[T](expr) {
  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}

