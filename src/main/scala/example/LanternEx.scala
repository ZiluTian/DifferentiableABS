package example

import scala.util.continuations._
import scala.util.continuations

import org.scala_lang.virtualized.virtualize
import org.scala_lang.virtualized.SourceContext

import scala.virtualization.lms._
import scala.virtualization.lms.common._ 

import scala.collection.mutable.ListBuffer

import scala.util.Random

// Examples from Lantern repo
object LanternEx extends App {
    object Example {
        private val addedExamples: ListBuffer[Example] = new ListBuffer[Example]()
        def add(f: => Unit): Unit = addedExamples.append(
            new Example {
                def run(): Unit = f
            }
        )
        def runAll(): Unit = addedExamples.foreach(e => e.run())
    }

    trait Example {
        def run(): Unit
    }

    // pass. Condition, reverse mode
    Example.add({
        val gr = new DslDriverScala[Double,Double] with DiffApi {
            def snippet(x: Rep[Double]): Rep[Double] = {
                val minus_1 = (new NumR(-1.0, var_new(0.0)))
                gradR(x => IF (x.x > 0.0) { minus_1*x } { x })(x)
            }
        }
        def grad(x: Double) = if (x > 0) -1 else 1
        for (x <- (-10 until 100)) {
          assert(gr.eval(x) == grad(x))
        }
    })

    // fail, adding random condition
    Example.add({
        val gr = new DslDriverScala[Double,Double] with DiffApi {
            def snippet(x: Rep[Double]): Rep[Double] = {
                val minus_1 = (new NumR(-1.0, var_new(0.0)))
                gradR(x => IF (Random.nextBoolean()) { minus_1*x } { x })(x)
            }
        }
        def grad(x: Double) = if (x > 0) -1 else 1
        //zt fail. Random.nextBoolean is evaluated dynamically, not statically
        // for (x <- (-10 until 100)) {
        //   assert(gr.eval(x) == grad(x))
        // }
    })

    Example.add({
            // forward mode
            val g1 = new DslDriverScala[Double, Double] with DiffApi {
                def snippet(x: Rep[Double]): Rep[Double] = {
                    gradF(x => x + x*x*x)(x)
                }
            }

            def grad(x: Double) = 1 + 3 * x * x
            for (x <- (-5 until 5)) {
                assert (g1.eval(x) == grad(x))
            }
        }
    )

    Example.add({
            // reverse mode
            val gr1 = new DslDriverScala[Double,Double] with DiffApi {
                def snippet(x: Rep[Double]): Rep[Double] = {
                    gradR(x => x + x*x*x)(x)
                }
            }
            def grad(x: Double) = 1 + 3 * x * x

            for (x <- (-5 until 5)) {
                assert (gr1.eval(x) == grad(x))
            }
        }
    )

    // function compositions
    Example.add({
            // reverse mode
            val gr1 = new DslDriverScala[Double,Double] with DiffApi {
                def snippet(x: Rep[Double]): Rep[Double] = {
                    def f1(x: NumR) = x * x * x
                    def f2(x: NumR) = x * x
                    def f3(x: NumR) = f2(f1(x))
                    gradR(f3)(x)
                }
            }
            def grad(x: Double) = 6 * x * x * x * x * x

            for (x <- (-5 until 5)) {
                assert (gr1.eval(x) == grad(x))
            }
    })

    Example.add({
            // forward mode
            val gr1 = new DslDriverScala[Double,Double] with DiffApi {
                def snippet(x: Rep[Double]): Rep[Double] = {
                    def f1(x: NumF) = x * x * x
                    def f2(x: NumF) = x * x
                    def f3(x: NumF) = f2(f1(x))
                    gradF(f3)(x)
                }
            }
            def grad(x: Double) = 6 * x * x * x * x * x

            for (x <- (-5 until 5)) {
                assert (gr1.eval(x) == grad(x))
            }
    })

    Example.add({
                val gr3 = new DslDriverScala[Double,Double] with DiffApi {
                def snippet(x: Rep[Double]): Rep[Double] = {
                    val half = (new NumR(0.5,var_new(0.0)))
                    val res = gradR(x => LOOP(x)(_.x > 1.0)(_ * half))(x)
                    // println(readVar(half.d))
                    res
                }
                }

                // System.out.println(gr3.code)
                /*****************************************
                 Emitting Generated Code
                *******************************************
                class Snippet extends ((Double)=>(Double)) {
                def apply(x0:Double): Double = {
                    val k = { x: Double => 1.0 }
                    var loop: [scala.Function1[Double,Double]] = {x10: (Double) =>
                    var x11: Double = 0.0
                    if (x10 > 1.0) {
                        val x13 = 0.5 * x10
                        x11 += 0.5 * loop(x13)
                    } else {
                        x11 += k(x10)
                    }
                    x11
                    }
                    loop(x0)
                }
                }
                *****************************************
                End of Generated Code
                *******************************************/
                // Hand-coded correct derivative
                def gfr(x: Double): Double = {
                    if (x > 1.0) 0.5 * gfr(0.5 * x) else 1.0
                }
                for (x <- (-5 until 5)) {
                    assert(gr3.eval(x) == gfr(x))
                }

    })

    Example.runAll
}
