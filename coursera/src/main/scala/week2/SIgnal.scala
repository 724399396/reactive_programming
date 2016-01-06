package week2

/**
  * @author liww.li
  */
class StackVariable[T](init: List[T]) {
  private var value = init
  def head = value.head
  def withValue(newValue: T)(op: => Unit): Unit = {
    value = newValue :: value
    try op finally value = value.init
  }
}

class Signal[T](t: => T) {
  import Signal._
  private var curValue: T = _
  private var curExpr: () => T = _
  private var observers: Set[Signal[T]] = _
  private var observered: Set[Signal[T]] = _
  update(t)

  def update(exp: => T): Unit = {
    curExpr = () => exp
    computeNext()
  }

  def computeNext(): Unit = {
    for(observer <- observered)
      observer.observers -= this
    caller.withValue(this)(curExpr)
    val obs = observers
    observers = Set()
    obs.foreach(_.computeNext())
  }

  def apply(): T = {
    observers = observers + caller.head
    assert(!caller.head.observers.contains(this))
    curValue
  }
}

object Signal {
  val caller = new StackVariable[Signal[_]](List(NoSignal))
  def apply[T](exp: => T) = new Signal(exp)
}

object NoSignal extends Signal[Nothing](***) {
  ???
}

