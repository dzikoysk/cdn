package net.dzikoysk.cdn.annotation

import panda.std.reactive.Reference
import panda.utilities.StringUtils
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class KotlinMemberResolver : MemberResolver {

    override fun fromField(instance: Any, field: Field): AnnotatedMember =
        FieldMember(instance, field)

    override fun fromProperty(instance: Any, propertyName: String) : AnnotatedMember {
        val find = instance::class.memberProperties.find { it.name == propertyName }

        if (find !is KMutableProperty<*>) {
            val getter = instance::class.functions.find { it.name == "get" + StringUtils.capitalize(propertyName) }
            val setter = instance::class.functions.find { it.name == "set" + StringUtils.capitalize(propertyName) }

            if (getter == null || setter == null) {
                throw NoSuchMethodException()
            }

            return KFunctionMember(instance, getter, setter, propertyName)
        }

        return KPropertyMember(instance, find)
    }

    override fun getFields(instance: Any): List<AnnotatedMember> =
        emptyList() // properties already contain fields, so we don't want to duplicate these references

    override fun getProperties(instance: Any): List<AnnotatedMember> =
        instance::class.memberProperties
            .map { Pair(it, findIndex(instance::class.java, it)) }
            .sortedBy { (_, index) -> index }
            .mapNotNull { (property) ->
                KPropertyMember(instance, property)
                /*when {
                    property is KProperty<*> -> KPropertyMember(instance, property)
                    property.returnType.isSubtypeOf(Reference::class.createType(listOf(KTypeProjection.STAR))) -> KPropertyMember(instance, property)
                    else -> null
                }*/
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