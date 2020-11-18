package io.github.wjur.avsc2avdl.api

import io.github.wjur.avsc2avdl.adapter.filesystem.DefaultFileLoader
import io.github.wjur.avsc2avdl.adapter.json.kotlinx.KotlinxSchemaConverter
import io.github.wjur.avsc2avdl.domain.SchemaConverter
import io.github.wjur.avsc2avdl.domain.SchemaPrinter
import io.github.wjur.avsc2avdl.domain.FileLoader

class Avsc2AvdlFacade(
    private val fileLoader: FileLoader,
    private val schemaConverter: SchemaConverter,
    private val schemaPrinter: SchemaPrinter
) {
    fun convert(avscFileName: String): String {
        val jsonSchema = fileLoader.loadFile(avscFileName)
        val schema = schemaConverter.convert(jsonSchema)
        return schemaPrinter.writeString(schema)
    }

    companion object {
        val INSTANCE = Avsc2AvdlFacade(
            DefaultFileLoader(),
            KotlinxSchemaConverter(),
            SchemaPrinter()
        )
    }
}
