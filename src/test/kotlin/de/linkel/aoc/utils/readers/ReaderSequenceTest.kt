package de.linkel.aoc.utils.readers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReaderSequenceTest {
    @Test
    fun `string with 4 characters streams out 4 characters`() {
        val ll = ReaderSequence("abcd".reader()).toList()
        Assertions.assertThat(ll.size).isEqualTo(4)
        Assertions.assertThat(ll[0]).isEqualTo('a')
        Assertions.assertThat(ll[1]).isEqualTo('b')
        Assertions.assertThat(ll[2]).isEqualTo('c')
        Assertions.assertThat(ll[3]).isEqualTo('d')
    }

    @Test
    fun `string with 4 characters streams first characters correct`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next()).isEqualTo('a')
    }

    @Test
    fun `string with 4 characters streams has no next after 4 chars`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        iterator.next()
        iterator.next()
        iterator.next()
        iterator.next()
        Assertions.assertThat(iterator.hasNext()).isFalse()
    }

    @Test
    fun `string with 4 characters throws error after 4 nexts`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        iterator.next()
        iterator.next()
        iterator.next()
        iterator.next()
        assertThrows<NoSuchElementException> { iterator.next() }
    }
}
