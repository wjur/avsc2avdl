package io.github.wjur.avsc2avdl.domain

interface SchemaConverter {
    fun convert(jsonString: String): Schema
}
