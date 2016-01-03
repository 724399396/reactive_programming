package week2

object test {

  object sim extends Circuits with Parameters
  import sim._
  val in1, in2, sum, carray = new Wire
  halfAdder(in1,in2,sum,carray)
  probe("sum", sum)
  probe("carray", carray)
  run()
}
