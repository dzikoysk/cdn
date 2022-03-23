package net.dzikoysk.cdn.reflect

import net.dzikoysk.cdn.CdnUtils
import panda.std.Blank
import panda.std.Option
import panda.std.Result
import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import java.util.Arrays
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaMethod

internal class KPropertyMember(
    private val property: KProperty<*>,
    private val resolver: MemberResolver
) : AnnotatedMember {

    override fun isIgnored(): Boolean =
        CdnUtils.isIgnored(property.javaField, false) || CdnUtils.isIgnored(property.getter.javaMethod)

    override fun setValue(instance: Any, value: Any): Result<Blank, ReflectiveOperationException> =
        Result
            .attempt(ReflectiveOperationException::class.java) { (property as KMutableProperty).setter.call(instance, value) }
            .mapToBlank()

    override fun getValue(instance: Any): Result<Option<Any>, ReflectiveOperationException> =
        Result.attempt(ReflectiveOperationException::class.java) {
            Option.of(property.getter.call(instance))
        }

    override fun isAnnotationPresent(annotation: Class<out Annotation>): Boolean =
        property.annotations.any { it.javaClass == annotation }
                || property.javaField?.isAnnotationPresent(annotation)
                ?: false

    override fun <A : Annotation?> getAnnotationsByType(annotation: Class<A>): MutableList<A> =
        PandaStream.of(property.javaGetter?.annotations, property.javaField?.annotations)
            .flatMapStream { Arrays.stream(it ?: emptyArray()) }
            .`is`(annotation)
            .toList()

    override fun <A : Annotation?> getAnnotation(annotation: Class<A>): A? =
        PandaStream.of(property.javaGetter?.annotations, property.javaField?.annotations)
            .flatMapStream { Arrays.stream(it ?: emptyArray()) }
            .`is`(annotation)
            .head()
            .orNull()

    override fun getTargetType(): TargetType =
        AnnotatedTargetType(annotatedType, resolver)

    override fun getAnnotatedType(): AnnotatedType =
        property.javaGetter?.annotatedReturnType ?: property.javaField!!.annotatedType

    override fun getType(): Class<*> =
        property.javaGetter?.returnType ?: property.javaField!!.type

    override fun getName(): String =
        property.name

}