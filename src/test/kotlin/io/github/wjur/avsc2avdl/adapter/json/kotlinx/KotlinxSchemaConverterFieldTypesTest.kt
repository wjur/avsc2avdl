package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import io.github.wjur.avsc2avdl.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class KotlinxSchemaConverterFieldTypesTest {

    private val converter = KotlinxSchemaConverter()

    @Test
    fun `should read string field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someStringField",
                  "type": "string"
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(StringTypeDef(null))
        assertThat(field.name).isEqualTo("someStringField")
    }

    @Test
    fun `should read complex string field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someStringField",
                  "type": {
                      "type": "string",
                      "avro.java.string": "string"
                  }
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(StringTypeDef("string"))
        assertThat(field.name).isEqualTo("someStringField")
    }

    @Test
    fun `should read int field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someIntField",
                  "type": "int"
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(IntTypeDef)
        assertThat(field.name).isEqualTo("someIntField")
    }

    @Test
    fun `should read long field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someLongField",
                  "type": "long"
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(LongTypeDef)
        assertThat(field.name).isEqualTo("someLongField")
    }

    @Test
    fun `should read boolean field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someBooleanField",
                  "type": "boolean"
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(BooleanTypeDef)
        assertThat(field.name).isEqualTo("someBooleanField")
    }

    @Test
    fun `should read union type field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someUnionField",
                  "type": [
                    "null",
                    "string",
                    "int",
                    "boolean"
                  ]
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(UnionTypeDef(listOf(
            NullTypeDef, StringTypeDef(), IntTypeDef, BooleanTypeDef
        )))
        assertThat(field.name).isEqualTo("someUnionField")
    }

    @Test
    fun `should read enum field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                "name": "someEnumField", 
                "type": {
                  "type": "enum",
                  "name": "EnumTypeName",
                  "doc": "Enum type docs",
                  "symbols": [
                    "ABC",
                    "XYZ",
                    "THIRD"
                  ]
                }
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(EnumTypeDef(
            "EnumTypeName", "Enum type docs", listOf("ABC", "XYZ", "THIRD")
        ))
        assertThat(field.name).isEqualTo("someEnumField")
    }

    @Test
    fun `should read field when type is referenced by name`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someRefTypeField",
                  "type": "SomeComplexType"
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(ReferenceByNameTypeDef("SomeComplexType"))
        assertThat(field.name).isEqualTo("someRefTypeField")
    }

    @Test
    fun `should read record field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someRecordField",
                  "type": {
                    "type": "record",
                    "name": "RecordTypeName",
                    "doc": "Record type docs",
                    "fields": [
                      {
                        "name": "strField",
                        "type": "string"
                      },
                      {
                        "name": "booleanField",
                        "type": "boolean"
                      }
                    ]
                  }
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(RecordTypeDef(
            "RecordTypeName", "Record type docs", listOf(
                Field("strField", null, StringTypeDef(), null),
                Field("booleanField", null, BooleanTypeDef, null),
            )
        ))
        assertThat(field.name).isEqualTo("someRecordField")
    }

    @Test
    fun `should read map field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someMapField",
                  "type": {
                    "type": "map",
                    "values": "SomeComplexType"
                  }
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(MapTypeDef(ReferenceByNameTypeDef("SomeComplexType")))
        assertThat(field.name).isEqualTo("someMapField")
    }

    @Test
    fun `should read array field`() {
        // given
        @Language("JSON5") val fieldString =
            """
            [
              {
                  "name": "someArrayField",
                  "type": {
                    "type": "array",
                    "items": "SomeComplexType"
                  }
              }
            ]
        """.trimIndent()


        // when
        val schema = converter.convert(someSchema(fieldString))

        // then
        val field = schema.fields.first()
        assertThat(field.type).isEqualTo(ArrayTypeDef(ReferenceByNameTypeDef("SomeComplexType")))
        assertThat(field.name).isEqualTo("someArrayField")
    }

    private fun someSchema(fields: String): String =
        """
             {
                "type": "record",
                "name": "ObjectName",
                "namespace": "this.is.the.namespace",
                "doc": "Super object for test purposes",
                "fields": $fields
            }
        """.trimIndent()
}
