package samples

import scala.concurrent.stm._
import java.util.{TimerTask, Timer}
import java.util.concurrent.TimeUnit

/**
 * Created by Leo on 5/27/14.
 */
object CoffeePot {
    val start = System.nanoTime()

    val cups = Ref(24)

    def fillCup(numberOfCups: Int) = {
        atomic.withRetryTimeout(6, TimeUnit.SECONDS) {
            implicit txn =>
                if (cups.get < numberOfCups) {
                    println("retry........" + numberOfCups + "  at  " + (System.nanoTime() - start) / 1.0e9)
                    retry(txn)
                }
                cups.swap(cups.get - numberOfCups)
                println("filled  up...." + numberOfCups)
                println("........  at  " + (System.nanoTime() - start) / 1.0e9)
        }
    }

    def main(args: Array[String]): Unit = {
        val timer = new Timer(true)
        timer.schedule(new TimerTask() {
            def run() {
                atomic {
                    implicit txn =>
                        println("Refilling....  at  " + (System.nanoTime() - start) / 1.0e9)
                        cups.swap(24)
                }
            }
        }, 5000)
        fillCup(20)
        fillCup(10)
        try {
            fillCup(22)
        } catch {
            case ex: Exception => {
                println("Failed:")
                ex.printStackTrace()
            }
        }
    }
}
