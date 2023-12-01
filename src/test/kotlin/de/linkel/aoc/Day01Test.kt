package de.linkel.aoc

import de.linkel.aoc.base.QuizPart
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class Day01Test {
    val exampleA = """
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
        """.trimIndent()
    val exampleB = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
        """.trimIndent()

    @Test
    fun `example part 1`() {
        Assertions.assertThat(Day01().test(QuizPart.A, exampleA)).isEqualTo(77)
    }

    @Test
    fun `example part 2`() {
        Assertions.assertThat(Day01().test(QuizPart.B, exampleB)).isEqualTo(281)
    }

    @Test
    fun `solution part 1`() {
        Assertions.assertThat(Day01().solve(QuizPart.A)).isEqualTo(54597)
    }

    @Test
    fun `solution part 2`() {
        Assertions.assertThat(Day01().solve(QuizPart.B)).isEqualTo(54504)
    }
}
