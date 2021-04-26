package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import io.github.wjur.avsc2avdl.domain.ArrayTypeDef
import io.github.wjur.avsc2avdl.domain.BooleanTypeDef
import io.github.wjur.avsc2avdl.domain.DefaultBoolean
import io.github.wjur.avsc2avdl.domain.DefaultEmptyArray
import io.github.wjur.avsc2avdl.domain.DefaultEmptyMap
import io.github.wjur.avsc2avdl.domain.DefaultNull
import io.github.wjur.avsc2avdl.domain.DefaultNumber
import io.github.wjur.avsc2avdl.domain.DefaultString
import io.github.wjur.avsc2avdl.domain.DefaultValue
import io.github.wjur.avsc2avdl.domain.EnumTypeDef
import io.github.wjur.avsc2avdl.domain.Field
import io.github.wjur.avsc2avdl.domain.IntTypeDef
import io.github.wjur.avsc2avdl.domain.LongTypeDef
import io.github.wjur.avsc2avdl.domain.MapTypeDef
import io.github.wjur.avsc2avdl.domain.NullTypeDef
import io.github.wjur.avsc2avdl.domain.RecordTypeDef
import io.github.wjur.avsc2avdl.domain.ReferenceByNameTypeDef
import io.github.wjur.avsc2avdl.domain.Schema
import io.github.wjur.avsc2avdl.domain.SchemaReader
import io.github.wjur.avsc2avdl.domain.StringTypeDef
import io.github.wjur.avsc2avdl.domain.TypeDef
import io.github.wjur.avsc2avdl.domain.UnionTypeDef
import io.github.wjur.avsc2avdl.domain.UserDataType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

val json = Json { ignoreUnknownKeys = true }

class KotlinxSchemaReader : SchemaReader {
    override fun read(jsonString: String): Schema {

        val avscSchema = json.decodeFromString<AvscSchema>(jsonString)
        assert(avscSchema.type == "record") {
            "Root type should be record but ${avscSchema.type} found"
        }
        return Schema(
            name = avscSchema.name,
            namespace = avscSchema.namespace,
            documentation = avscSchema.doc,
            fields = avscSchema.fields.map { it.toSchemaField() }
        )
    }
}

private fun AvscField.toSchemaField(): Field {
    val schemaType = type.toSchemaTypeDef()

    return Field(
        name = this.name,
        documentation = this.doc,
        type = schemaType,
        default = default.toSchemaDefault(),
        userDataType = userDataType?.let(::UserDataType)
    )
}

private fun JsonElement?.toSchemaDefault(): DefaultValue? {
    if (this == AvscField.NO_DEFAULT) return null
    return when(this) {
        null -> DefaultNull
        JsonNull -> DefaultNull
        is JsonPrimitive -> {
            val booleanV = this.booleanOrNull?.let { DefaultBoolean(it) }
            val longV = this.longOrNull?.let { DefaultNumber(it) }
            val floatV = this.floatOrNull?.let { DefaultNumber(it)}
            val stringV = this.takeIf { it.isString }?.let { DefaultString(it.content) }
            booleanV ?: longV ?: floatV ?: stringV
        }
        is JsonArray -> {
            require(this.isEmpty())
            DefaultEmptyArray
        }
        is JsonObject -> {
            require(this.isEmpty())
            DefaultEmptyMap
        }
        else -> throw IllegalArgumentException(this.toString())
    }
}

private fun JsonElement.toSchemaTypeDef(): TypeDef {
    return when (this) {
        is JsonPrimitive -> this.content.toSchemaPrimitiveType()
        JsonNull -> NullTypeDef
        is JsonObject -> {
            when (this.getValue("type").jsonPrimitive.content) {
                "int" -> this.toIntTypeDef()
                "long" -> this.toLongTypeDef()
                "boolean" -> this.toBooleanTypeDef()
                "array" -> this.toArrayTypeDef()
                "record" -> this.toRecordTypeDef()
                "enum" -> this.toEnumTypeDef()
                "map" -> this.toMapTypeDef()
                "string" -> this.toStringTypeDef()
                else -> throw IllegalArgumentException(this.toString())
            }
        }
        is JsonArray -> UnionTypeDef(this.map { it.toSchemaTypeDef() })
    }
}

private fun JsonObject.toEnumTypeDef(): EnumTypeDef {
    val name = this["name"]?.jsonPrimitive?.content!!
    val documentation = this["doc"]?.jsonPrimitive?.content
    val symbols = this["symbols"]?.jsonArray?.map { it.jsonPrimitive.content }!!
    return EnumTypeDef(name, documentation, symbols)
}

private fun JsonObject.toMapTypeDef(): MapTypeDef {
    return MapTypeDef(
        this["values"]?.toSchemaTypeDef()!!,
        this["java-class"]?.jsonPrimitive?.content,
        this["java-key-class"]?.jsonPrimitive?.content,
    )
}

private fun JsonObject.toIntTypeDef(): IntTypeDef {
    return IntTypeDef(this["java-class"]?.jsonPrimitive?.content)
}

private fun JsonObject.toLongTypeDef(): LongTypeDef {
    return LongTypeDef(this["java-class"]?.jsonPrimitive?.content)
}

private fun JsonObject.toBooleanTypeDef(): BooleanTypeDef {
    return BooleanTypeDef(this["java-class"]?.jsonPrimitive?.content)
}

private fun JsonObject.toStringTypeDef(): StringTypeDef {
    return StringTypeDef(
        this["java-class"]?.jsonPrimitive?.content
    )
}

private fun JsonObject.toArrayTypeDef(): ArrayTypeDef {
    return ArrayTypeDef(
        this["items"]?.toSchemaTypeDef()!!,
        this["java-class"]?.jsonPrimitive?.content,
        this["java-key-class"]?.jsonPrimitive?.content,
    )
}

private fun JsonObject.toRecordTypeDef(): RecordTypeDef {
    val name = this["name"]?.jsonPrimitive?.content!!
    val documentation = this["doc"]?.jsonPrimitive?.content
    val fields = this["fields"]?.jsonArray?.map {
        json.decodeFromJsonElement<AvscField>(it).toSchemaField()
    }
    return RecordTypeDef(
        name = name,
        documentation = documentation,
        fields = fields ?: emptyList()
    )
}

private fun String.toSchemaPrimitiveType(): TypeDef {
    return when (this) {
        "null" -> NullTypeDef
        "int" -> IntTypeDef()
        "long" -> LongTypeDef()
        "string" -> StringTypeDef()
        "boolean" -> BooleanTypeDef()
        else -> ReferenceByNameTypeDef(this)
    }
}
