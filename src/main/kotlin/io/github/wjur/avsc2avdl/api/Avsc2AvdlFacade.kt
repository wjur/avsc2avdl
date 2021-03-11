package io.github.wjur.avsc2avdl.api

import io.github.wjur.avsc2avdl.adapter.filesystem.DefaultFileLoader
import io.github.wjur.avsc2avdl.adapter.json.kotlinx.KotlinxSchemaReader
import io.github.wjur.avsc2avdl.domain.FileLoader
import io.github.wjur.avsc2avdl.domain.SchemaConverter
import io.github.wjur.avsc2avdl.domain.SchemaPrinter

class Avsc2AvdlFacade(
    private val fileLoader: FileLoader,
    private val schemaConverter: SchemaConverter
) {
    fun convert(avscFileName: String): String =
        schemaConverter.convert(jsonSchema = fileLoader.loadFile(avscFileName))

    companion object {
        val INSTANCE = Avsc2AvdlFacade(
            DefaultFileLoader(),
            SchemaConverter(KotlinxSchemaReader(), SchemaPrinter())
        )
    }
}
