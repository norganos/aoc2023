package de.linkel.aoc.utils.readers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PeekingReaderTest {
    @Test
    fun `read returns correct content`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
    }

    @Test
    fun `data peeked can still be read`() {
        val reader = "hallo welt".reader().peeking()
        Assertions.assertThat(reader.peek(10)).isEqualTo("hallo welt")
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
    }

    @Test
    fun `data can be peeked multiple times`() {
        val reader = "hallo welt".reader().peeking()
        Assertions.assertThat(reader.peek(10)).isEqualTo("hallo welt")
        Assertions.assertThat(reader.peek(10)).isEqualTo("hallo welt")
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
    }

    @Test
    fun `peeked is reset after read`() {
        val reader = "hallo welt".reader().peeking()
        Assertions.assertThat(reader.peek(5)).isEqualTo("hallo")
        val buf = CharArray(5)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 5))).isEqualTo("hallo")
        Assertions.assertThat(reader.peek(5)).isEqualTo(" welt")
    }

    @Test
    fun `multiple consecutive peeks can extend the buffer`() {
        val reader = "hallo welt".reader().peeking()
        Assertions.assertThat(reader.peek(5)).isEqualTo("hallo")
        Assertions.assertThat(reader.peek(10)).isEqualTo("hallo welt")
        Assertions.assertThat(reader.peek(5)).isEqualTo("hallo")
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
    }

    @Test
    fun `peek on empty buffer returns empty`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
        Assertions.assertThat(reader.peek(5)).isEqualTo("")
    }

    @Test
    fun `peek on ended buffer returns empty`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
        reader.read()
        Assertions.assertThat(reader.peek(5)).isEqualTo("")
    }

    @Test
    fun `peek on fresh empty buffer returns empty`() {
        val reader = "".reader().peeking()
        Assertions.assertThat(reader.peek(5)).isEqualTo("")
    }
}
