package net.dzikoysk.cdn.annotation

import panda.std.reactive.Reference
import panda.utilities.StringUtils
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

class KotlinMemberResolver : MemberResolver {

    override fun fromField(instance: Any, field: Field): AnnotatedMember {
        return FieldMember(instance, field)
    }

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

    override fun getProperties(instance: Any): List<AnnotatedMember> {
        return instance::class.memberProperties.mapNotNull {
            when {
                it is KMutableProperty<*> -> KPropertyMember(instance, it)
                it.returnType.isSubtypeOf(Reference::class.createType(listOf(KTypeProjection.STAR))) -> KPropertyMember(instance, it)
                else -> null
            }
        }
    }

}