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
