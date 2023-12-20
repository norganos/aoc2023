package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.lcm
import jakarta.inject.Singleton

@Singleton
class Day20: AbstractLinesAdventDay<Long>() {
    override val day = 20

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val parsedModules = lines
            .map { line ->
                val spec = line.substringBefore(" ")
                val next = line.substringAfter(" -> ").split(",").map(String::trim)
                if (spec == "broadcaster") {
                    Broadcaster(spec, next)
                } else when(spec[0]) {
                    '%' -> FlipFlop(spec.substring(1), next)
                    '&' -> Conjunction(spec.substring(1), next)
                    else -> throw Exception("unknown module spec $spec")
                }
            }
            .toList()
        val button = Button("button", listOf("broadcaster"))
        val rx = Rx("rx")
        val modules = parsedModules + button + rx
        val nops = (modules.flatMap { it.next }.distinct().toSet() - modules.map { it.name })
            .map { Nop(it) }
        val dispatcher = CommunicationDispatcher(modules + nops)
        dispatcher.init()

        if (part == QuizPart.A) {
            repeat(1000) {
                button.press()
                while (dispatcher.busy) {
                    dispatcher.tick()
                }
            }
            return dispatcher.lowPulses * dispatcher.highPulses
        } else {
            return if (modules.any { "rx" in it.next }) {
                val feeder = modules.first { it.name == rx.inputs.first() } as Conjunction
                var buttonPresses = 0L
                val cycles = feeder.lastSignals
                    .mapValues {
                        0L
                    }
                    .toMutableMap()
                while (true) {
                    rx.reset()
                    buttonPresses++
                    button.press()
                    while (dispatcher.busy) {
                        dispatcher.tick()
                    }
                    feeder.gotHigh
                        .forEach {
                            if (it.value && cycles[it.key] == 0L) {
                                cycles[it.key] = buttonPresses
                            }
                        }
                    if (cycles.all { it.value > 0L }) {
                        break
                    }
                }
                return cycles.values.fold(1L) { a, b -> lcm(a, b) }
            } else 0L
        }

    }

    data class Message(
        val sender: String,
        val receivers: List<String>,
        val pulse: Boolean
    )
    class CommunicationDispatcher(
        modules: Collection<Module>
    ) {
        private val modules = modules.associateBy { it.name }

        private val queue = mutableListOf<Message>()
        private var lowPulseCounter = 0L
        private var highPulseCounter = 0L
        val lowPulses get(): Long = lowPulseCounter
        val highPulses get(): Long = highPulseCounter
        val busy get() = queue.isNotEmpty()
        fun init() {
            modules.values
                .onEach { it.dispatcher = this }
                .flatMap { module -> module.next.map { module.name to it } }
                .groupBy { it.second }
                .mapValues { entry -> entry.value.map { it.first } }
                .forEach { (module, prev) ->
                    modules[module]?.wireUp(prev) ?: throw Exception("module $module not found")
                }
        }
        fun dispatch(sender: String, pulse: Boolean, receivers: List<String>) {
            queue.add(Message(sender, receivers, pulse))
        }
        fun tick() {
            if (queue.isNotEmpty()) {
                queue.removeAt(0)
                    .let { message ->
                        message.receivers
                            .map { modules[it]!! }
//                            .onEach { println("${message.sender} -${if (message.pulse) "high" else "low"}-> ${it.name}") }
                            .onEach { if (message.pulse) highPulseCounter++ else lowPulseCounter++ }
                            .forEach { it.receive(message.sender, message.pulse) }
                    }
            }
        }
    }

    abstract class Module(
        val name: String,
        val next: List<String>
    ) {
        var dispatcher: CommunicationDispatcher? = null
        protected fun send(pulse: Boolean) {
            dispatcher?.dispatch(name, pulse, next)
        }
        open fun wireUp(prev: List<String>) {
        }
        abstract fun receive(sender: String, pulse: Boolean)
    }

    class FlipFlop(
        name: String,
        next: List<String>
    ): Module(name, next) {
        var state = false
        override fun receive(sender: String, pulse: Boolean) {
            if (!pulse) {
                state = !state
                send(state)
            }
        }

        override fun toString(): String = "FlipFlop $name"
    }

    class Conjunction(
        name: String,
        next: List<String>
    ): Module(name, next) {
        private var state = mutableMapOf<String, Boolean>()
        val gotHigh = mutableMapOf<String, Boolean>()
        val lastSignals get(): Map<String, Boolean> = state
        override fun wireUp(prev: List<String>) {
            prev.forEach { state[it] = false }
            prev.forEach { gotHigh[it] = false }
        }
        override fun receive(sender: String, pulse: Boolean) {
            state[sender] = pulse
            if (pulse) {
                gotHigh[sender] = true
            }
            send(state.values.any { !it })
        }

        override fun toString(): String = "Conjunction $name"
    }

    class Broadcaster(
        name: String,
        next: List<String>
    ): Module(name, next) {
        override fun receive(sender: String, pulse: Boolean) {
            send(pulse)
        }

        override fun toString(): String = "Broadcaster $name"
    }
    class Rx(
        name: String
    ): Module(name, emptyList()) {
        private val states = mutableListOf<Boolean>()
        val lastSignals get(): List<Boolean> = states

        private var prev = emptyList<String>()
        val inputs get(): List<String> = prev

        fun reset() {
            states.clear()
        }

        override fun wireUp(prev: List<String>) {
            this.prev = prev
        }

        override fun receive(sender: String, pulse: Boolean) {
            states.add(pulse)
        }

        override fun toString(): String = "Rx $name"
    }

    class Nop(
        name: String
    ): Module(name, emptyList()) {
        override fun receive(sender: String, pulse: Boolean) {
        }

        override fun toString(): String = "Nop $name"
    }

    class Button(
        name: String,
        next: List<String>
    ): Module(name, next) {
        fun press() {
            if (dispatcher?.busy == false)
                send(false)
        }
        override fun receive(sender: String, pulse: Boolean) {
        }

        override fun toString(): String = "Button $name"
    }
}
