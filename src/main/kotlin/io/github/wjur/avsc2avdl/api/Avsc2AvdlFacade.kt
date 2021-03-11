package io.github.wjur.avsc2avdl.api

import io.github.wjur.avsc2avdl.adapter.filesystem.DefaultFileLoader
import io.github.wjur.avsc2avdl.adapter.json.kotlinx.KotlinxSchemaReader
import io.github.wjur.avsc2avdl.domain.SchemaReader
import io.github.wjur.avsc2avdl.domain.SchemaPrinter
import io.github.wjur.avsc2avdl.domain.FileLoader

class Avsc2AvdlFacade(
    private val fileLoader: FileLoader,
    private val schemaReader: SchemaReader,
    private val schemaPrinter: SchemaPrinter
) {
    fun convert(avscFileName: String): String {
        val jsonSchema = fileLoader.loadFile(avscFileName)
        val schema = schemaReader.read(jsonSchema)
        return schemaPrinter.writeString(schema)
    }

    companion object {
        val INSTANCE = Avsc2AvdlFacade(
            DefaultFileLoader(),
            KotlinxSchemaReader(),
            SchemaPrinter()
        )
    }
}
