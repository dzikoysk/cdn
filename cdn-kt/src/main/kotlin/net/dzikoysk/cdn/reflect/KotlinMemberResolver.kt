package net.dzikoysk.cdn.reflect

import panda.utilities.StringUtils
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class KotlinMemberResolver(private val scopeVisibility: Visibility = Visibility.PRIVATE) : MemberResolver {

    override fun fromField(type: Class<*>, field: Field): AnnotatedMember =
        FieldMember(field, this)

    override fun fromProperty(type: Class<*>, propertyName: String) : AnnotatedMember {
        val kType = type.kotlin
        val find = type.kotlin.memberProperties.find { it.name == propertyName }

        if (find !is KMutableProperty<*>) {
            val getter = kType.functions.find { it.name == "get" + StringUtils.capitalize(propertyName) }
            val setter = kType.functions.find { it.name == "set" + StringUtils.capitalize(propertyName) }

            if (getter == null || setter == null) {
                throw NoSuchMethodException()
            }

            return KFunctionMember(propertyName, getter, setter, this)
        }

        return KPropertyMember(find, this)
    }

    override fun getFields(type: Class<*>): List<AnnotatedMember> =
        emptyList() // properties already contain fields, so we don't want to duplicate these references

    override fun getProperties(type: Class<*>): List<AnnotatedMember> =
        type.kotlin.memberProperties
            .map { Pair(it, findIndex(type, it)) }
            .sortedBy { (_, index) -> index }
            .map { (property) -> KPropertyMember(property, this) }

    override fun getScopeVisibility(): Visibility {
        return this.scopeVisibility
    }

    /**
     * This method attempts to recreate order of properties in sources.
     * The whole idea bases on the fact, that in most cases, the order of fields stays the same.
     */
    private fun findIndex(rootType: Class<*>, property: KProperty<*>): Int {
        var type = rootType
        var offset = 0

        while (type != Object::class.java) {
            val fields = type.declaredFields
            val index = fields.indexOf(property.javaField)

            if (index != -1) {
                return offset + index
            }

            offset += fields.size
            type = type.superclass
        }

        return -1
    }

}