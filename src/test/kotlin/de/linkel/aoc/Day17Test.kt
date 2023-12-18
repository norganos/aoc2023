package de.linkel.aoc

class Day17Test: AbstractDayTest<Int>() {
    override val exampleA = """
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
        """.trimIndent()
    override val exampleSolutionA = 102
    override val solutionA = 956

    override val exampleSolutionB = 94
    override val solutionB = 1106

    override val implementation = Day17()
}
