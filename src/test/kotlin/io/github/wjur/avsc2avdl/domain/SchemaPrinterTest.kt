package io.github.wjur.avsc2avdl.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SchemaPrinterTest {

    private val printer = SchemaPrinter()

    @Test
    fun `should print basic schema information`() {
        // given
        val schema = Schema(
            "SchemaName",
            "some.namespace",
            "some schema docs",
            emptyList()
        )

        val expected = """@namespace("some.namespace")
protocol SchemaName {
    /** some schema docs */
    record SchemaName {

    }


}"""

        // expect
        assertThat(printer.writeString(schema)).isEqualTo(expected)
    }

    @Test
    fun `should print schema with string field`() {
        // given
        val field = Field(
            "testField",
            "this is test field doc",
            StringTypeDef(),
            DefaultString("some default value")
        )

        val expected = """@namespace("some.namespace")
protocol SchemaName {
    /** some schema docs */
    record SchemaName {
        /** this is test field doc */
        string testField = "some default value";
    }


}"""

        // expect
        assertThat(printer.writeString(schema(field))).isEqualTo(expected)
    }

    @Test
    fun `should print schema with int field`() {
        // given
        val field = Field(
            "testField",
            "this is test field doc",
            IntTypeDef,
            DefaultNumber(5)
        )

        val expected = """@namespace("some.namespace")
protocol SchemaName {
    /** some schema docs */
    record SchemaName {
        /** this is test field doc */
        int testField = 5;
    }


}"""

        // expect
        assertThat(printer.writeString(schema(field))).isEqualTo(expected)
    }

    @Test
    fun `should print schema with long field`() {
        // given
        val field = Field(
            "testField",
            "this is test field doc",
            LongTypeDef,
            DefaultNumber(5)
        )

        val expected = """@namespace("some.namespace")
protocol SchemaName {
    /** some schema docs */
    record SchemaName {
        /** this is test field doc */
        long testField = 5;
    }


}"""

        // expect
        assertThat(printer.writeString(schema(field))).isEqualTo(expected)
    }

    @Test
    fun `should print schema with boolean field`() {
        // given
        val field = Field(
            "testField",
            "this is test field doc",
            BooleanTypeDef,
            DefaultBoolean(true)
        )

        val expected = """@namespace("some.namespace")
protocol SchemaName {
    /** some schema docs */
    record SchemaName {
        /** this is test field doc */
        boolean testField = true;
    }


}"""

        // expect
        assertThat(printer.writeString(schema(field))).isEqualTo(expected)
    }

    private fun schema(field: Field): Schema =
        Schema(
            "SchemaName",
            "some.namespace",
            "some schema docs",
            listOf(field)
        )
}
