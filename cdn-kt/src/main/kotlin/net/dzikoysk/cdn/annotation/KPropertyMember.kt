package net.dzikoysk.cdn.annotation

import net.dzikoysk.cdn.CdnUtils
import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaMethod

internal class KPropertyMember(private val instance: Any, private val property: KProperty<*>) : AnnotatedMember {

    override fun isIgnored(): Boolean =
        CdnUtils.isIgnored(property.javaField, false) || CdnUtils.isIgnored(property.getter.javaMethod)

    override fun setValue(value: Any) {
        (property as KMutableProperty).setter.call(instance, value)
    }

    override fun getValue(): Any? =
        property.getter.call(instance)

    override fun isAnnotationPresent(annotation: Class<out Annotation>): Boolean =
        property.annotations.any { it.javaClass == annotation }
                || property.javaField?.isAnnotationPresent(annotation)
                ?: false

    override fun <A : Annotation?> getAnnotationsByType(annotation: Class<A>): MutableList<A> =
        PandaStream.of(property.javaField?.annotations)
            .flatMap { it.toList() }
            .`is`(annotation)
            .toList()

    override fun <A : Annotation?> getAnnotation(annotation: Class<A>): A? =
        PandaStream.of(property.javaField?.annotations)
            .flatMap { it.toList() }
            .`is`(annotation)
            .head()
            .orNull

    override fun getAnnotatedType(): AnnotatedType =
        property.javaGetter?.annotatedReturnType!!

    override fun getType(): Class<*> =
        property.javaGetter?.returnType!!

    override fun getName(): String =
        property.name

    override fun getInstance(): Any =
        instance

}