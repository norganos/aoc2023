package de.linkel.aoc

class Day08Test: AbstractDayTest<Long>() {
    override val exampleA = """
RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)
        """.trimIndent()
    val exampleA2 = """
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)
        """.trimIndent()
    override val exampleSolutionA = 2L
    override val solutionA = 14429L

    override val exampleB = """
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)
    """.trimIndent()

    override val exampleSolutionB = 6L
    override val solutionB = 10921547990923L

    override val implementation = Day08()
}
