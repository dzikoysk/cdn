package net.dzikoysk.cdn.annotation

import net.dzikoysk.cdn.CdnUtils
import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.*

class KFunctionMember(private val instance : Any, getter: Method, setter: Method) : AnnotatedMember {

    private val getter: KFunction<*> = getter.kotlinFunction as KFunction<*>
    private val setter: KFunction<*> = setter.kotlinFunction as KFunction<*>

    override fun setValue(value: Any?) {
        getter.call(instance, value)
    }

    override fun getValue(): Any? {
        return setter.call(instance)
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

    override fun getName(): String = CdnUtils.getPropertyNameFromMethod(getter.name)

    override fun getInstance(): Any = instance

}