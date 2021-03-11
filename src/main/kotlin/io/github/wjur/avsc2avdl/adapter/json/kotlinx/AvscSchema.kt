package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class AvscSchema(
    val type: String,
    val name: String,
    val namespace: String,
    val doc: String? = null,
    val fields: List<AvscField>
)

@Serializable
data class AvscField(
    val name: String,
    val doc: String? = null,
    val type: JsonElement,
    val default: JsonElement? = NO_DEFAULT,
    val userDataType: String? = null
) {
    companion object {
        val NO_DEFAULT = JsonPrimitive("NO_DEFAULT")
    }
}
