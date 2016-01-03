package week2

/**
  * Created by liwei on 2016/1/3.
  */
trait Circuits extends Gates {
  def halfAdder(a: Wire, b: Wire, out: Wire, c: Wire): Unit = {
    val e,f = new Wire
    andGate(a,b, out)
    orGate(a,b,e)
    inverter(e,f)
    andGate(f,out,c)
  }

}
