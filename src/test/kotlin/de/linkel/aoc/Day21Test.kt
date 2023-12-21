package de.linkel.aoc

class Day21Test: AbstractDayTest<Long>() {
    override val exampleA = """
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
        """.trimIndent()
    override val exampleSolutionA = 16L
    override val solutionA = 3773L

    override val exampleSolutionB = 16733044L
    override val solutionB = 625628021226274L

    override val implementation = Day21()
}
// 6872
// 3774
