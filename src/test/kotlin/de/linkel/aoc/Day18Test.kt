package de.linkel.aoc

class Day18Test: AbstractDayTest<Long>() {
    override val exampleA = """
R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)
        """.trimIndent()
    override val exampleSolutionA = 62L
    override val solutionA = 35991L

    override val exampleSolutionB = 952408144115L
    override val solutionB = 54058824661845L

    override val implementation = Day18()
}
/*

#######         7 0
#.....#         2 5
###...#         4 3
..#...#         2 3
..#...#         2 3
###.###         6 1
#...#..         2 3
##..###         5 2
.#....#         2 4
.######         6 0



 */
