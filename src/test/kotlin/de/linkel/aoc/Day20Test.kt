package de.linkel.aoc

class Day20Test: AbstractDayTest<Long>() {
    override val exampleA = """
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
        """.trimIndent()
    val exampleA2 = """
broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output
        """.trimIndent()

    override val exampleSolutionA = 32000000L
    override val solutionA = 832957356L

    override val exampleSolutionB = 0L
    override val solutionB = 240162699605221L

    override val implementation = Day20()
}
