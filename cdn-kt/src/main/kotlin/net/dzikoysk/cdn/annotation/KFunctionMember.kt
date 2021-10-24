package net.dzikoysk.cdn.annotation

import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.*

class KFunctionMember(private val instance: Any, private val getter: KFunction<*>, private val setter: KFunction<*>, private val propertyName: String) : AnnotatedMember {

    override fun setValue(value: Any?) {
        setter.call(instance, value)
    }

    override fun getValue(): Any? {
        return getter.call(instance)
    }

    override fun isAnnotationPresent(annotation: Class<out Annotation>?): Boolean {
        return getter.annotations
            .map { it.javaClass }
            .contains(annotation)
    }

    override fun <A : Annotation?> getAnnotationsByType(annotation: Class<A>?): MutableList<A>? {
        return PandaStream.of(getter.annotations)
            .`is`(annotation)
            .toList()
    }

    override fun <A : Annotation?> getAnnotation(annotation: Class<A>?): A? {
        return PandaStream.of(getter.annotations)
            .`is`(annotation)
            .head()
            .orNull
    }

    override fun getAnnotatedType(): AnnotatedType? = getter.javaMethod?.annotatedReturnType

    override fun getType(): Class<*>? = getter.javaMethod?.returnType

    override fun getName(): String = propertyName

    override fun getInstance(): Any = instance

}