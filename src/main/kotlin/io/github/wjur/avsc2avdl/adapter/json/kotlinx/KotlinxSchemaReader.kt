package io.github.wjur.avsc2avdl.adapter.json.kotlinx

import io.github.wjur.avsc2avdl.domain.*
import io.github.wjur.avsc2avdl.domain.BooleanTypeDef
import io.github.wjur.avsc2avdl.domain.IntTypeDef
import io.github.wjur.avsc2avdl.domain.LongTypeDef
import io.github.wjur.avsc2avdl.domain.NullTypeDef
import io.github.wjur.avsc2avdl.domain.RecordTypeDef
import io.github.wjur.avsc2avdl.domain.StringTypeDef
import io.github.wjur.avsc2avdl.domain.UnionTypeDef
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

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
        default = default.toSchemaDefault()
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
    return MapTypeDef(this["values"]?.toSchemaTypeDef()!!)
}

private fun JsonObject.toStringTypeDef(): StringTypeDef {
    return StringTypeDef(this["avro.java.string"]?.jsonPrimitive?.content!!)
}

private fun JsonObject.toArrayTypeDef(): ArrayTypeDef {
    return ArrayTypeDef(this["items"]?.toSchemaTypeDef()!!)
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
        "int" -> IntTypeDef
        "long" -> LongTypeDef
        "string" -> StringTypeDef()
        "boolean" -> BooleanTypeDef
        else -> ReferenceByNameTypeDef(this)
    }
}
