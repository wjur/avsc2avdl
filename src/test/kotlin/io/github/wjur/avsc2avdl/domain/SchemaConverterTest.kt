package io.github.wjur.avsc2avdl.domain

import io.github.wjur.avsc2avdl.adapter.json.kotlinx.KotlinxSchemaReader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Scanner

internal class SchemaConverterTest {

    private val converter = SchemaConverter(KotlinxSchemaReader(), SchemaPrinter())

    @Test
    fun `should convert simple schema`() {
        // given
        val input = fromResource("0001_input.avsc")
        val expectedOutput = fromResource("0001_output.avdl")

        // expect
        assertThat(converter.convert(input)).isEqualTo(expectedOutput)
    }

    @Test
    fun `should convert complicated schema`() {
        // given
        val input = fromResource("0002_input.avsc")
        val expectedOutput = fromResource("0002_output.avdl")

        // expect
        assertThat(converter.convert(input)).isEqualTo(expectedOutput)
    }

    private fun fromResource(name: String): String {
        val fileName = "schemaConverter/$name"
        val resource = SchemaConverterTest::class.java.classLoader.getResourceAsStream(fileName)!!
        return Scanner(resource, "UTF-8").useDelimiter("\\A").next()
    }
}
