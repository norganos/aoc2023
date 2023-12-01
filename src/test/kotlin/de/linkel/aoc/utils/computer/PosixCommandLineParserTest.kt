package de.linkel.aoc.utils.computer

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PosixCommandLineParserTest {
    private val parser = PosixCommandLineParser()

    private fun parse(input: String): CommandLine {
        return input.reader().use { reader ->
            parser.parse(reader)
        }
    }

    @Test
    fun `can parse ls`() {
        Assertions.assertThat(
            parse("ls")
        ).isEqualTo(CommandLine("ls"))
    }
    @Test
    fun `can parse simple cd upwards`() {
        Assertions.assertThat(
            parse("cd ..")
        ).isEqualTo(CommandLine("cd", listOf("..")))
    }
    @Test
    fun `can parse simple rm -rf dir with wildcard`() {
        Assertions.assertThat(
            parse("rm -rf /tmp/*")
        ).isEqualTo(CommandLine("rm", listOf("-rf", "/tmp/*")))
    }
    @Test
    fun `whitespaces at the beginning are ignores`() {
        Assertions.assertThat(
            parse("   cd /tmp/")
        ).isEqualTo(CommandLine("cd", listOf("/tmp/")))
    }
    @Test
    fun `whitespaces at the end are ignores`() {
        Assertions.assertThat(
            parse("cd /tmp/   ")
        ).isEqualTo(CommandLine("cd", listOf("/tmp/")))
    }
    @Test
    fun `whitespaces in the middle are ignored`() {
        Assertions.assertThat(
            parse("cd   /tmp/")
        ).isEqualTo(CommandLine("cd", listOf("/tmp/")))
    }
    @Test
    fun `multiple types of whitespaces in the middle are ignored`() {
        Assertions.assertThat(
            parse("cd\t  \t /tmp/")
        ).isEqualTo(CommandLine("cd", listOf("/tmp/")))
    }
    @Test
    fun `double quotes work with whitespaces and single quotes inside`() {
        Assertions.assertThat(
            parse("echo \"hallo 'welt'\"")
        ).isEqualTo(CommandLine("echo", listOf("hallo 'welt'")))
    }
    @Test
    fun `single quotes work with whitespaces and double quotes inside`() {
        Assertions.assertThat(
            parse("echo 'hallo \"welt\"'")
        ).isEqualTo(CommandLine("echo", listOf("hallo \"welt\"")))
    }
}
