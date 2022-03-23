package net.dzikoysk.cdn.reflect

import net.dzikoysk.cdn.CdnUtils
import panda.std.Blank
import panda.std.Blank.BLANK
import panda.std.Option
import panda.std.Result
import panda.std.stream.PandaStream
import java.lang.reflect.AnnotatedType
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.*

internal class KFunctionMember(
    private val propertyName: String,
    private val getter: KFunction<*>,
    private val setter: KFunction<*>,
    private val resolver: MemberResolver
) : AnnotatedMember {

    override fun isIgnored(): Boolean =
        CdnUtils.isIgnored(setter.javaMethod) || CdnUtils.isIgnored(getter.javaMethod)

    override fun setValue(instance: Any, value: Any): Result<Blank, ReflectiveOperationException> =
        Result
            .attempt(ReflectiveOperationException::class.java) { setter.call(instance, value) ?: BLANK }
            .mapToBlank()

    override fun getValue(instance: Any): Result<Option<Any>, ReflectiveOperationException> =
        Result.attempt(ReflectiveOperationException::class.java) {
            Option.of(getter.call(instance))
        }

    override fun isAnnotationPresent(annotation: Class<out Annotation>): Boolean =
        getter.annotations
            .map { it.javaClass }
            .contains(annotation)

    override fun <A : Annotation> getAnnotationsByType(annotation: Class<A>): List<A> =
        PandaStream.of(getter.annotations)
            .`is`(annotation)
            .toList()

    override fun <A : Annotation> getAnnotation(annotation: Class<A>): A? =
        PandaStream.of(getter.annotations)
            .`is`(annotation)
            .head()
            .orNull()

    override fun getTargetType(): TargetType =
        AnnotatedTargetType(annotatedType, resolver)

    override fun getAnnotatedType(): AnnotatedType =
        getter.javaMethod!!.annotatedReturnType

    override fun getType(): Class<*> =
        getter.javaMethod!!.returnType

    override fun getName(): String =
        propertyName

}