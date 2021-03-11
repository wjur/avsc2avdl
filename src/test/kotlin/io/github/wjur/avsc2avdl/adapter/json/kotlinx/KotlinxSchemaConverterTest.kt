package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class KotlinxSchemaReaderTest {

    private val converter = KotlinxSchemaReader()

    @Test
    fun `should read schema header`() {
        // given
        val schemaString = """
            {
                "type": "record",
                "name": "ObjectName",
                "namespace": "this.is.the.namespace",
                "doc": "Super object for test purposes",
                "fields": []
            }
        """.trimIndent()

        // when
        val schema = converter.read(schemaString)

        // then
        assertThat(schema.name).isEqualTo("ObjectName")
        assertThat(schema.namespace).isEqualTo("this.is.the.namespace")
        assertThat(schema.documentation).isEqualTo("Super object for test purposes")
    }

    @Test
    fun `should read field doc`() {
        // given
        @Language("JSON5") val schemaString = """
            {
                "type": "record",
                "name": "ObjectName",
                "namespace": "this.is.the.namespace",
                "doc": "Super object for test purposes",
                "fields": [
                  {
                    "name": "someField",
                    "type": "string",
                    "doc": "This is someField docs!"
                  }
                ]
            }
        """.trimIndent()

        // when
        val schema = converter.read(schemaString)

        // then
        assertThat(schema.fields.first().documentation).isEqualTo("This is someField docs!")
    }
}
