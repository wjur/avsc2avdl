package io.github.wjur.avsc2avdl.domain

data class Schema(
    val name: String,
    val namespace: String,
    override val documentation: String?,
    val fields: List<Field>
) : Documentable

data class Field(
    val name: String,
    override val documentation: String?,
    val type: TypeDef,
    val default: DefaultValue?,
    val userDataType: UserDataType? = null
) : Documentable

data class UserDataType(val value: String)

sealed class DefaultValue
object DefaultNull : DefaultValue()
data class DefaultString(val value: String) : DefaultValue()
data class DefaultNumber(val value: Number) : DefaultValue()
data class DefaultBoolean(val value: Boolean): DefaultValue()
object DefaultEmptyArray : DefaultValue()
object DefaultEmptyMap : DefaultValue()


sealed class TypeDef

object NullTypeDef : TypeDef()
data class IntTypeDef(val stringableJavaClass: String? = null) : TypeDef()
data class LongTypeDef(val stringableJavaClass: String? = null) : TypeDef()
data class StringTypeDef(val stringableJavaClass: String? = null) : TypeDef()
data class BooleanTypeDef(val stringableJavaClass: String? = null) : TypeDef()
data class UnionTypeDef(val types: List<TypeDef>) : TypeDef()
data class RecordTypeDef(
    val name: String,
    override val documentation: String?,
    val fields: List<Field>
) : TypeDef(), Documentable
data class MapTypeDef(
    val valueType: TypeDef,
    val stringableJavaClass: String? = null,
    val stringableKeyJavaClass: String? = null) : TypeDef()
data class ArrayTypeDef(
    val itemType: TypeDef,
    val stringableJavaClass: String? = null,
    val stringableKeyJavaClass: String? = null
) : TypeDef()
data class EnumTypeDef(
    val name: String,
    override val documentation: String?,
    val symbols: List<String>
) : TypeDef(), Documentable
data class ReferenceByNameTypeDef(val name: String) : TypeDef()

interface Documentable {
    val documentation: String?
}
