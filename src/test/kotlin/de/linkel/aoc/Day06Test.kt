package de.linkel.aoc

class Day06Test: AbstractDayTest<Long>() {
    override val exampleA = """
Time:      7  15   30
Distance:  9  40  200
        """.trimIndent()
    override val exampleSolutionA = 288L
    override val solutionA = 1108800L

    override val exampleSolutionB = 71503L
    override val solutionB = 36919753L

    override val implementation = Day06()
}
