package example

object TestExample extends App {
  // Refunctionalized tape
  // global refunctionalized tape
  var tape = (u: Unit) => ()

  // tape = ((x: Unit) => println("This is the first statement")) andThen tape
  // tape = ((x: Unit) => println("This is the second statement")) andThen tape
  // tape()  // backward pass, print second statement first and then first

  class NumB(val x: Double, var d: Double) {
    def +(that: NumB): NumB = {
      val y = new NumB(this.x + that.x, 0.0)
      tape = ((x: Unit) => this.d += y.d) andThen tape
      tape = ((x: Unit) => that.d += y.d) andThen tape
      y
    }

    def *(that: NumB): NumB = {
      val y = new NumB(this.x * that.x, 0.0)
      tape = ((x: Unit) => this.d += that.x * y.d) andThen tape
      tape = ((x: Unit) => that.d += this.x * y.d) andThen tape
      y
    }
  }

  def grad(f: NumB => NumB)(x: Double) = {
    val z = new NumB(x, 0.0)
    f(z).d = 1.0
    tape()
    z.d
  }

  val testEq: NumB => NumB = {
    (x: NumB) => new NumB(2, 0.0)*x + x*x*x
  }
  def expectedRes(x: Double) = 2 + 3*x*x

  for (i <- Range(1, 20)) {
    assert(grad(testEq)(i) == expectedRes(i))
  }


}