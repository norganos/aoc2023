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

/*
solving AoC 2023 Day 6 A
Solution is 288
calculation took 21ms           14
solving AoC 2023 Day 6 B
Solution is 71503
calculation took 23ms           3
solving AoC 2023 Day 6 A
Solution is 1108800
calculation took 2ms            0
solving AoC 2023 Day 6 B
Solution is 36919753            0
calculation took 153ms

 */
