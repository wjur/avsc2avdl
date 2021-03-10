package io.github.wjur.avsc2avdl.domain


class SchemaPrinter {
    fun writeString(schema: Schema): String {
        return """@namespace("${schema.namespace}")
protocol ${schema.name} {
${schema.writeDocString(0)}${tabs(0)}record ${schema.name} {
${schema.fields.writeFieldsString(1)}${tabs(0)}
${tabs(0)}}

${schema.fields.writeTypesString(0)}
}"""
    }
}

private fun UnionTypeDef.subRecords(): Sequence<PrintableClass> {
    return this.types.asSequence()
        .flatMap { it.subRecords() }
}

private fun RecordTypeDef.subRecords(): Sequence<PrintableClass> {
    return sequenceOf(PrintableRecord(this)) + this.fields.asSequence()
        .flatMap { it.type.subRecords() }
}

private fun ArrayTypeDef.subRecords(): Sequence<PrintableClass> {
    return itemType.subRecords()
}

private fun MapTypeDef.subRecords(): Sequence<PrintableClass> {
    return valueType.subRecords()
}

private fun TypeDef.subRecords(): Sequence<PrintableClass> {
    return when (this) {
        is ReferenceByNameTypeDef,
        NullTypeDef,
        IntTypeDef,
        LongTypeDef,
        is StringTypeDef,
        BooleanTypeDef -> emptySequence()
        is EnumTypeDef -> sequenceOf(PrintableEnum(this))
        is UnionTypeDef -> this.subRecords()
        is RecordTypeDef -> this.subRecords()
        is MapTypeDef -> this.subRecords()
        is ArrayTypeDef -> this.subRecords()
    }
}

private fun List<Field>.writeTypesString(level: Int): String {
    return this.asSequence()
        .flatMap { it.type.subRecords() }
        .map { it.writeString(level) }
        .joinToString("\n\n")
}

private fun Documentable.writeDocString(level: Int): String {
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
        is DefaultEmptyMap -> " = {}"
        is DefaultEmptyArray -> " = []"
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
        is StringTypeDef -> "string"
        BooleanTypeDef -> "boolean"
        is UnionTypeDef -> """union { ${type1.types.joinToString(", ") { writeTypeName(it) }} }"""
        is RecordTypeDef -> type1.name
        is ReferenceByNameTypeDef -> type1.name
        is MapTypeDef -> "map<${writeTypeName(type1.valueType)}>"
        is ArrayTypeDef -> "array<${writeTypeName(type1.itemType)}>"
        is EnumTypeDef -> type1.name
    }
}

private fun tabs(level: Int): String = (0..level).joinToString("") { "    " }

private interface PrintableClass {
    fun writeString(level: Int): String
}

data class PrintableRecord(val record: RecordTypeDef) : PrintableClass {
    override fun writeString(level: Int): String {
        return """${record.writeDocString(level)}${tabs(level)}record ${record.name} {
${record.fields.writeFieldsString(level + 1)}
${tabs(level)}}"""
    }
}

data class PrintableEnum(val enum: EnumTypeDef) : PrintableClass {
    override fun writeString(level: Int): String {
        return """${enum.writeDocString(level)}${tabs(level)}enum ${enum.name} {
${enum.symbols.writeSymbolsString(level + 1)}
${tabs(level)}}"""
    }

}

private fun List<String>.writeSymbolsString(level: Int): String {
    return this.joinToString(",\n") { symbolName -> """${tabs(level)}${symbolName}""" }
}
