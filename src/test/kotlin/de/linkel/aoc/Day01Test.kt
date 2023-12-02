package de.linkel.aoc

class Day01Test: AbstractDayTest<Int>() {
    override val exampleA = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
        """.trimIndent()
    override val exampleSolutionA = 142
    override val solutionA = 54597


    override val exampleB = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
        """.trimIndent()
    override val exampleSolutionB = 281
    override val solutionB = 54504

    override val implementation = Day01()
}
