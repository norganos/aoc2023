package de.linkel.aoc.utils.ring

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException

class RingTest {

    @Test
    fun `new ring is empty`() {
        val ring = Ring<String>()
        Assertions.assertThat(ring.isEmpty()).isTrue()
        Assertions.assertThat(ring.isNotEmpty()).isFalse()
        Assertions.assertThat(ring).isEmpty()
    }

    @Test
    fun `iterator of empty ring has no next`() {
        val ring = Ring<String>()
        Assertions.assertThat(ring.iterator().hasNext()).isFalse()
        assertThrows<NoSuchElementException> { ring.iterator().next() }
    }

    @Test
    fun `empty ring does not contain any element`() {
        val ring = Ring<String>()
        Assertions.assertThat(ring.contains("a")).isFalse()
    }

    @Test
    fun `if we add a element, the ring is not empty any more`() {
        val ring = Ring<String>()
        ring.add("a")
        Assertions.assertThat(ring.isEmpty()).isFalse()
        Assertions.assertThat(ring.isNotEmpty()).isTrue()
        Assertions.assertThat(ring).hasSize(1)
        Assertions.assertThat(ring.iterator().hasNext()).isTrue()
    }

    @Test
    fun `if we add a element, it is contained`() {
        val ring = Ring<String>()
        ring.add("a")
        Assertions.assertThat(ring.contains("a")).isTrue()
    }

    @Test
    fun `if we add a element, it points to itself`() {
        val ring = Ring<String>()
        ring.add("a")
        val elem = ring.first()
        Assertions.assertThat(elem.payload).isEqualTo("a")
        Assertions.assertThat(elem.next).isEqualTo(elem)
        Assertions.assertThat(elem.prev).isEqualTo(elem)
    }

    @Test
    fun `if we add a element, the iterator returns only that element`() {
        val ring = Ring<String>()
        ring.add("a")
        val iterator = ring.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next()).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isFalse()
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `if we remove the last element, the ring is empty again`() {
        val ring = Ring<String>()
        ring.add("a")
        Assertions.assertThat(ring.remove("a")).isTrue()
        Assertions.assertThat(ring.isEmpty()).isTrue()
        Assertions.assertThat(ring.isNotEmpty()).isFalse()
        Assertions.assertThat(ring).isEmpty()
    }

    @Test
    fun `if we try to remove something that is not in the ring, nothing happens`() {
        val ring = Ring<String>()
        ring.add("a")
        Assertions.assertThat(ring.remove("b")).isFalse()
        Assertions.assertThat(ring.isNotEmpty()).isTrue()
        Assertions.assertThat(ring.isEmpty()).isFalse()
        Assertions.assertThat(ring).hasSize(1)
    }

    @Test
    fun `if we add two elements they point to each other`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        Assertions.assertThat(ring).hasSize(2)
        Assertions.assertThat(b.next).isEqualTo(a)
        Assertions.assertThat(b.prev).isEqualTo(a)
        Assertions.assertThat(a.next).isEqualTo(b)
        Assertions.assertThat(a.prev).isEqualTo(b)
    }

    @Test
    fun `if we add two elements the iterator returns both`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        Assertions.assertThat(ring.iterator().toList().toSet()).isEqualTo(setOf("a", "b"))
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "b"))
        Assertions.assertThat(b.iterator().toList().map { it.payload }).isEqualTo(listOf("b", "a"))
    }

    @Test
    fun `if we remove one of two elements the ring only contains the remaining one`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        b.remove()
        Assertions.assertThat(ring).hasSize(1)
        Assertions.assertThat(ring.iterator().toList()).isEqualTo(listOf("a"))
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a"))
        assertThrows<IllegalStateException> { b.iterator() }
    }

    @Test
    fun `if the have many elements, we can traverse`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        Assertions.assertThat(a.next).isEqualTo(b)
        Assertions.assertThat(b.next).isEqualTo(c)
        Assertions.assertThat(c.next).isEqualTo(d)
        Assertions.assertThat(d.next).isEqualTo(a)
        Assertions.assertThat(a.prev).isEqualTo(d)
        Assertions.assertThat(b.prev).isEqualTo(a)
        Assertions.assertThat(c.prev).isEqualTo(b)
        Assertions.assertThat(d.prev).isEqualTo(c)
        Assertions.assertThat(a[1]).isEqualTo(b)
        Assertions.assertThat(a[-1]).isEqualTo(d)
        Assertions.assertThat(a[2]).isEqualTo(c)
        Assertions.assertThat(a[4]).isEqualTo(a)
        Assertions.assertThat(a[-3]).isEqualTo(b)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "b", "c", "d"))
    }

    @Test
    fun `if the have many elements, we can move elements right`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        b.moveRight()
        Assertions.assertThat(b.next).isEqualTo(d)
        Assertions.assertThat(d.prev).isEqualTo(b)
        Assertions.assertThat(c.next).isEqualTo(b)
        Assertions.assertThat(b.prev).isEqualTo(c)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "c", "b", "d"))
        Assertions.assertThat(b.iterator().toList().map { it.payload }).isEqualTo(listOf("b", "d", "a", "c"))
    }

    @Test
    fun `if the have many elements, we can move elements multiple steps to the right`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        ring.add("c")
        val d = ring.add("d")
        b.moveRight(2)
        Assertions.assertThat(b.next).isEqualTo(a)
        Assertions.assertThat(a.prev).isEqualTo(b)
        Assertions.assertThat(d.next).isEqualTo(b)
        Assertions.assertThat(b.prev).isEqualTo(d)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "c", "d", "b"))
        Assertions.assertThat(b.iterator().toList().map { it.payload }).isEqualTo(listOf("b", "a", "c", "d"))
    }

    @Test
    fun `if the have many elements, we can move elements left`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        ring.add("c")
        val d = ring.add("d")
        b.moveLeft()
        Assertions.assertThat(b.next).isEqualTo(a)
        Assertions.assertThat(a.prev).isEqualTo(b)
        Assertions.assertThat(d.next).isEqualTo(b)
        Assertions.assertThat(b.prev).isEqualTo(d)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "c", "d", "b"))
        Assertions.assertThat(b.iterator().toList().map { it.payload }).isEqualTo(listOf("b", "a", "c", "d"))
    }

    @Test
    fun `if the have many elements, we can move elements multiple steps to the left`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        b.moveLeft(2)
        Assertions.assertThat(b.next).isEqualTo(d)
        Assertions.assertThat(d.prev).isEqualTo(b)
        Assertions.assertThat(c.next).isEqualTo(b)
        Assertions.assertThat(b.prev).isEqualTo(c)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "c", "b", "d"))
        Assertions.assertThat(b.iterator().toList().map { it.payload }).isEqualTo(listOf("b", "d", "a", "c"))
    }

    @Test
    fun `if the have many elements, we can insert elements on specific positions from ring element api`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        ring.add("d")
        val x = b.append("x")
        Assertions.assertThat(b.next).isEqualTo(x)
        Assertions.assertThat(x.prev).isEqualTo(b)
        Assertions.assertThat(x.next).isEqualTo(c)
        Assertions.assertThat(c.prev).isEqualTo(x)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "b", "x", "c", "d"))
    }

    @Test
    fun `if the have many elements, we can insert elements on specific positions from ring api`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        ring.add("d")
        val x = ring.insertAfter("x", b)
        Assertions.assertThat(b.next).isEqualTo(x)
        Assertions.assertThat(x.prev).isEqualTo(b)
        Assertions.assertThat(x.next).isEqualTo(c)
        Assertions.assertThat(c.prev).isEqualTo(x)
        Assertions.assertThat(a.iterator().toList().map { it.payload }).isEqualTo(listOf("a", "b", "x", "c", "d"))
    }

    @Test
    fun `containsAll works as expected`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        ring.add("c")
        Assertions.assertThat(ring.containsAll(listOf("a", "b"))).isTrue()
        Assertions.assertThat(ring.containsAll(listOf("c"))).isTrue()
        Assertions.assertThat(ring.containsAll(listOf("c", "d"))).isFalse()
        Assertions.assertThat(ring.containsAll(emptyList())).isTrue()
    }

    @Test
    fun `ring iterator fails if new element is added concurrently`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        val iterator = ring.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next()).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        ring.add("c")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if new element is added concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        val iterator = a.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next().payload).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        ring.add("c")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring iterator fails if element is removed concurrently`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = ring.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next()).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        ring.remove("c")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if element is removed concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = a.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next().payload).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        ring.remove("c")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring iterator fails if element is moved concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = ring.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next()).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        a.moveLeft()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if element is moved concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = a.iterator()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        Assertions.assertThat(iterator.next().payload).isEqualTo("a")
        Assertions.assertThat(iterator.hasNext()).isTrue()
        a.moveLeft()
        Assertions.assertThat(iterator.hasNext()).isTrue()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `distance is calculated correctly`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        Assertions.assertThat(a.distanceTo(a)).isEqualTo(0)
        Assertions.assertThat(a.distanceTo(b)).isEqualTo(1)
        Assertions.assertThat(b.distanceTo(a)).isEqualTo(-1)
        Assertions.assertThat(a.distanceTo(d)).isEqualTo(-1)
        Assertions.assertThat(a.distanceTo(c)).isEqualTo(2)
        Assertions.assertThat(a[a.distanceTo(b)]).isEqualTo(b)
    }

    private fun <T> Iterator<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        while(this.hasNext()) {
            result.add(this.next())
        }
        return result
    }
}
