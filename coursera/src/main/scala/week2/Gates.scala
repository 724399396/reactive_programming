package week2

/**
  * Created by liwei on 2016/1/3.
  */
trait Gates extends Simulation {
  def inverterDelay:Int
  def orGateDelay:Int
  def andGateDelay:Int

  class Wire {
    private var signal: Boolean = _
    private var actions: List[Action] = List()

    def getSignal: Boolean = signal

    def setSignal(sig: Boolean) = {
      if (signal != sig) {
        signal = sig
        actions foreach (_())
      }
    }

    def addAction(action: Action): Unit = {
      actions = action :: actions
    }
  }

  def inverter(in: Wire, out: Wire): Unit = {
    def inverterAction() = {
      val signal = in.getSignal
      afterDelay(inverterDelay)(out.setSignal(signal))
    }
    in addAction inverterAction
  }

  def andGate(a: Wire, b: Wire, out: Wire): Unit = {
    def andAction() = {
      val signal = a.getSignal & b.getSignal
      afterDelay(andGateDelay)(out.setSignal(signal))
    }
    a addAction andAction
    b addAction andAction
  }

  def orGate(a: Wire, b: Wire, out: Wire): Unit = {
    def orAction() = {
      val signal = a.getSignal | b.getSignal
      afterDelay(andGateDelay)(out.setSignal(signal))
    }
    a addAction orAction
    b addAction orAction
  }

  def probe(name: String, wire: Wire): Unit = {
    afterDelay(0) {
      println(s"$name at $getCurTime() changed to ${wire.getSignal}")
    }
  }
}
