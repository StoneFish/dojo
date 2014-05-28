package samples

import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.stm._

/**
 * Created by Leo on 5/23/14.
 */
class EnergySource {

    private val MAXLEVEL = 100L
    val level = Ref(MAXLEVEL)
    val usageCount = Ref(0L)
    val keepRunning = Ref(true)

    private def init() = {
        EnergySource.replenishTimer.schedule(new Runnable() {
            override def run() = {
                replenish
                atomic {
                    implicit txn =>
                        if (keepRunning.get) {
                            EnergySource.replenishTimer.schedule(this, 1, TimeUnit.SECONDS)
                        }
                }
            }
        }, 1, TimeUnit.SECONDS)
    }


    def stopEnergySource() = atomic {
        implicit txn => keepRunning.swap(false)
    }

    def getUnitsAvailable() = atomic {
        implicit txn => level.get
    }

    def getUsageCount() = atomic {
        implicit txn => usageCount.get
    }

    def useEnergy(units: Long) = {
        atomic {
            implicit txn =>
                val currentLevel = level.get
                if (units > 0 && currentLevel >= units) {
                    level.swap(currentLevel - units)
                    usageCount.swap(usageCount.get + 1)
                    true
                } else false
        }
    }

    private def replenish() = atomic {
        implicit txn =>
            if (level.get(txn) < MAXLEVEL) level.swap(level.get + 1)
    }

}

object EnergySource {
    val replenishTimer = Executors.newScheduledThreadPool(10)

    def create() = {
        val energySource = new EnergySource
        energySource.init
        energySource
    }
}
