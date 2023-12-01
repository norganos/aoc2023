package de.linkel.aoc.utils

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TopListTest {
    @Test
    fun `top 3 list with 3 elements works`() {
        val top3 = TopList<Int>(3).plus(listOf(1, 2, 5, 3, 4)).toList()
        Assertions.assertThat(top3).hasSize(3)
        Assertions.assertThat(top3).contains(5)
        Assertions.assertThat(top3).contains(4)
        Assertions.assertThat(top3).contains(3)
        Assertions.assertThat(top3.first()).isEqualTo(5)
    }

    @Test
    fun `top 3 list with 1 element works`() {
        val top3 = TopList<Int>(3).plus(listOf(23)).toList()
        Assertions.assertThat(top3).hasSize(1)
        Assertions.assertThat(top3).contains(23)
    }
}
