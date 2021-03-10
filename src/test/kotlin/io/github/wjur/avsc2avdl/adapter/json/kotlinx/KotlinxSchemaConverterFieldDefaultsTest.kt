package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import io.github.wjur.avsc2avdl.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class KotlinxSchemaConverterFieldDefaultsTest {

    private val converter = KotlinxSchemaConverter()

    @Test
    fun `should read null default`() {
        // given
        val defaultString = "null"

        // when
        val schema = converter.convert(someSchemaWithField(defaultString))

        // then
        assertThat(schema.fields.first().default).isEqualTo(DefaultNull)
    }

    @Test
    fun `should read string default`() {
        // given
        val defaultString = """ "someValue" """

        // when
        val schema = converter.convert(someSchemaWithField(defaultString))

        // then
        assertThat(schema.fields.first().default).isEqualTo(DefaultString("someValue"))
    }

    @Test
    fun `should read number default`() {
        // given
        val defaultString = "123"

        // when
        val schema = converter.convert(someSchemaWithField(defaultString))

        // then
        assertThat((schema.fields.first().default as DefaultNumber).value.toLong()).isEqualTo(123L)
    }

    @Test
    fun `should read boolean default`() {
        // given
        val defaultString = "true"

        // when
        val schema = converter.convert(someSchemaWithField(defaultString))

        // then
        assertThat(schema.fields.first().default).isEqualTo(DefaultBoolean(true))
    }


    private fun someSchemaWithField(defaultString: String): String =
        """
             {
                "type": "record",
                "name": "ObjectName",
                "namespace": "this.is.the.namespace",
                "doc": "Super object for test purposes",
                "fields": [
                  {
                      "name": "someStringField",
                      "type": "string",
                      "default": $defaultString
                  }
                ]
            }
        """.trimIndent()
}
