package de.linkel.aoc.utils.readers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class EndAwareReaderTest {
    @Test
    fun `fresh reader with data has not ended`() {
        val reader = "abc".reader().endAware()
        Assertions.assertThat(reader.hasEnded).isFalse()
    }

    @Test
    fun `already used reader with data left has not ended`() {
        val reader = "abc".reader().endAware()
        reader.read()
        Assertions.assertThat(reader.hasEnded).isFalse()
    }

    @Test
    fun `fully consumed reader with no data left has not ended yet`() {
        val reader = "abc".reader().endAware()
        reader.read(CharArray(5), 0, 5)
        Assertions.assertThat(reader.hasEnded).isFalse()
    }

    @Test
    fun `fully consumed reader with no data left is marked as ended after tried read`() {
        val reader = "abc".reader().endAware()
        reader.read(CharArray(5), 0, 5)
        reader.read()
        Assertions.assertThat(reader.hasEnded).isTrue()
    }

    @Test
    fun `fresh empty reader is marked as ended after tried read`() {
        val reader = "".reader().endAware()
        reader.read()
        Assertions.assertThat(reader.hasEnded).isTrue()
    }
}
