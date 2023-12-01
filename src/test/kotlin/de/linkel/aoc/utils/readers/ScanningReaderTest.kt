package de.linkel.aoc.utils.readers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ScanningReaderTest {
    @Test
    fun `without delimiters it reads the full content`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(emptyList())
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo welt")
        Assertions.assertThat(base.read()).isEqualTo(-1)
    }

    @Test
    fun `with delimiter it stops at the index`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(listOf(" "))
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo")
    }

    @Test
    fun `with delimiter it stops at the index and the underlying reader continues after the delimiter`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(listOf(" "))
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo")
        Assertions.assertThat(String(buf, 0, base.read(buf, 0, 10))).isEqualTo("welt")
    }

    @Test
    fun `with delimiters it stops at the first found delimiter`() {
        val base = "hallo\twelt und\n so".reader().peeking()
        val reader = base.scan(listOf(" ", "\t", "\n", "---"))
        val buf = CharArray(10)
        Assertions.assertThat(String(buf, 0, reader.read(buf, 0, 10))).isEqualTo("hallo")
    }
}
