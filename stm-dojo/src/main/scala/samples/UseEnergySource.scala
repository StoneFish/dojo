package samples

import java.util.concurrent.{Callable, Executors}
import java.util
import akka.actor._

/**
 * Created by Leo on 5/23/14.
 */
object UseEnergySource {
    val energySource = EnergySource.create()

    val service = Executors.newFixedThreadPool(10);

    def main(args: Array[String]) {
        println("Energy  level  at  start:  " + energySource.getUnitsAvailable())

        val tasks = new util.ArrayList[Callable[String]]()

        for (i <- 1 to 10) {
            tasks.add(new Callable[String] {
               def call() = {
                   for (j <- 1 to 7) {
                       energySource.useEnergy(1)
                   }
                   ""
                }
            })
        }

        service.invokeAll(tasks);

        println("Energy  level  at  end:  " + energySource.getUnitsAvailable())
        println("Usage:  " + energySource.getUsageCount())
        energySource.stopEnergySource()
    }
}
