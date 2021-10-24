package net.dzikoysk.cdn.annotation

import java.lang.reflect.Field
import java.lang.reflect.Method

class KotlinAnnotationResolver : AnnotationResolver {

    override fun createMember(instance: Any, field: Field): AnnotatedMember {
        return KPropertyMember(instance, field)
    }

    override fun createFunction(instance: Any, getter: Method, setter: Method): AnnotatedMember {
        return KFunctionMember(instance, getter, setter)
    }

}