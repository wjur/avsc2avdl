package io.github.wjur.avsc2avdl.domain

interface FileLoader {
    fun loadFile(fileName: String): String
}
