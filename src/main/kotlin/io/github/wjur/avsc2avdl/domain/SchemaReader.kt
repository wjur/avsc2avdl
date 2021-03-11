package io.github.wjur.avsc2avdl.domain

interface SchemaReader {
    fun read(jsonString: String): Schema
}
