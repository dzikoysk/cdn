package net.dzikoysk.cdn.annotation

import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

class KPropertyMember(private val instance: Any, private val  property: KMutableProperty<*>) : AnnotatedMember {

    override fun setValue(value: Any?) {
        property.setter.call(instance, value)
    }

    override fun getValue(): Any? {
        return property.getter.call(instance)
    }

    override fun isAnnotationPresent(annotation: Class<out Annotation>?): Boolean {
        return property.annotations
            .map { it.javaClass }
            .contains(annotation)
    }

    override fun <A : Annotation?> getAnnotationsByType(annotation: Class<A>?): MutableList<A>? {
        return PandaStream.of(property.javaField?.annotations)
            .flatMap { it.toList() }
            .`is`(annotation)
            .toList()
    }

    override fun <A : Annotation?> getAnnotation(annotation: Class<A>?): A? {
        return PandaStream.of(property.javaField?.annotations)
            .flatMap { it.toList() }
            .`is`(annotation)
            .head()
            .orNull
    }

    override fun getAnnotatedType(): AnnotatedType? = property.javaGetter?.annotatedReturnType

    override fun getType(): Class<*>? = property.javaField?.type

    override fun getName(): String = property.name

    override fun getInstance(): Any = instance

}