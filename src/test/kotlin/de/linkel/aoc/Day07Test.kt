package de.linkel.aoc

class Day07Test: AbstractDayTest<Long>() {
    override val exampleA = """
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
        """.trimIndent()
    override val exampleSolutionA = 6440L
    override val solutionA = 251287184L

    override val exampleSolutionB = 5905L
    override val solutionB = 250757288L

    override val implementation = Day07()
}
/*
solving AoC 2023 Day 7 A
Solution is 6440
calculation took 21,7ms
solving AoC 2023 Day 7 B
Solution is 5905
calculation took 0,8ms
solving AoC 2023 Day 7 A
Solution is 251287184
calculation took 58,0ms
solving AoC 2023 Day 7 B
Solution is 250757288
calculation took 7,9ms
 */
