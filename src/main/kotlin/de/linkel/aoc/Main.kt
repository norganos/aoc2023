package de.linkel.aoc

import de.linkel.aoc.base.AdventDay
import de.linkel.aoc.base.QuizPart
import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.*
import kotlin.system.exitProcess

@Command(name = "aoc", description = ["..."])
class Main : Runnable {
    @Inject
    lateinit var days: List<AdventDay<*>>

    @Parameters(index = "0", defaultValue = "0")
    var day: Int = 0

    @Parameters(index = "1..*")
    var args: List<String> = emptyList()

    override fun run() {
        if (day == 0) {
            val today = Calendar.getInstance()
            if (today.get(Calendar.MONTH) == Calendar.DECEMBER) {
                day = today.get(Calendar.DAY_OF_MONTH)
            }
        }
        val adventDay = days.firstOrNull{ it.day == day }
        if (adventDay == null) {
            println("implementation for day $day not found")
            exitProcess(1)
        }
        for (part in QuizPart.entries) {
            adventDay.solve(part, args)
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(Main::class.java, *args)
        }
    }
}
