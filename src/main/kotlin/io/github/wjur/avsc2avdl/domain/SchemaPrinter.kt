package io.github.wjur.avsc2avdl.domain

import java.lang.StringBuilder

class SchemaPrinter {
    fun writeString(schema: Schema): String {
        return """@namespace("${schema.namespace}")
protocol ${schema.name} {
${tabs(0)}record ${schema.name} {
${schema.fields.writeFieldsString(1)}${tabs(0)}
${tabs(0)}}

${schema.fields.writeTypesString(0)}
}"""
    }
}

private fun UnionTypeDef.subRecords(): Sequence<RecordTypeDef> {
    return this.types.asSequence()
        .flatMap { it.subRecords() }
}

private fun RecordTypeDef.subRecords(): Sequence<RecordTypeDef> {
    return sequenceOf(this) + this.fields.asSequence()
        .flatMap { it.type.subRecords() }
}

private fun ArrayTypeDef.subRecords(): Sequence<RecordTypeDef> {
    return itemType.subRecords()
}

private fun MapTypeDef.subRecords(): Sequence<RecordTypeDef> {
    return valueType.subRecords()
}

private fun TypeDef.subRecords(): Sequence<RecordTypeDef> {
    return when (this) {
        is ReferenceByNameTypeDef,
        NullTypeDef,
        IntTypeDef,
        LongTypeDef,
        StringTypeDef,
        is EnumTypeDef,
        BooleanTypeDef -> emptySequence()
        is UnionTypeDef -> this.subRecords()
        is RecordTypeDef -> this.subRecords()
        is MapTypeDef -> this.subRecords()
        is ArrayTypeDef -> this.subRecords()
    }
}

private fun List<Field>.writeTypesString(level: Int): String {
    return this.asSequence()
        .flatMap { it.type.subRecords() }
        .map {
            """${it.writeDocString(level)}${tabs(level)}record ${it.name} {
${it.fields.writeFieldsString(level + 1)}
${tabs(level)}}
"""
        }.joinToString("\n\n")
}

private fun RecordTypeDef.writeDocString(level: Int): String {
    return if (this.documentation != null) "${tabs(level)}/** ${this.documentation} */\n" else ""
}

private fun List<Field>.writeFieldsString(level: Int): String {
    return this.map {
        """${it.writeDocString(level)}${tabs(level)}${it.writeTypeName()} ${it.name}${it.writeDefault()};"""
    }.joinToString("\n\n")
}

private fun Field.writeDefault(): String {
    return when (default) {
        null -> ""
        is DefaultNull -> " = null"
        is DefaultString -> " = \"${default.value}\""
        is DefaultNumber -> " = ${default.value}"
        is DefaultBoolean -> " = ${default.value}"
    }
}

private fun Field.writeTypeName(): String {
    return writeTypeName(type)
}

private fun writeTypeName(type1: TypeDef): String {
    return when (type1) {
        NullTypeDef -> "null"
        IntTypeDef -> "int"
        LongTypeDef -> "long"
        StringTypeDef -> "string"
        BooleanTypeDef -> "boolean"
        is UnionTypeDef -> """union { ${type1.types.joinToString(", ") { writeTypeName(it) }} }"""
        is RecordTypeDef -> type1.name
        is ReferenceByNameTypeDef -> type1.name
        is MapTypeDef -> "map<${writeTypeName(type1.valueType)}>"
        is ArrayTypeDef -> "array<${writeTypeName(type1.itemType)}>"
        is EnumTypeDef -> type1.name
    }
}

private fun Field.writeDocString(level: Int): String? {
    return if (this.documentation != null) "${tabs(level)}/** ${this.documentation} */\n" else ""
}

private fun tabs(level: Int): String = (0..level).joinToString("") { "  " }
