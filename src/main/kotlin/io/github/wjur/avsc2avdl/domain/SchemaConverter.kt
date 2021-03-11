package io.github.wjur.avsc2avdl.domain

class SchemaConverter(
    private val schemaReader: SchemaReader,
    private val schemaPrinter: SchemaPrinter
) {
    fun convert(jsonSchema: String): String {
        val schema = schemaReader.read(jsonSchema)
        return schemaPrinter.writeString(schema)
    }
}
