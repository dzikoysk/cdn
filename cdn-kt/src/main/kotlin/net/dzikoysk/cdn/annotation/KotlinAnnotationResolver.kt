package net.dzikoysk.cdn.annotation

import java.lang.reflect.Field
import java.lang.reflect.Method

class KotlinAnnotationResolver : AnnotationResolver {

    override fun fromField(instance: Any, field: Field): AnnotatedMember {
        return KPropertyMember(instance, field)
    }

    override fun fromProperty(instance: Any, getter: Method, setter: Method): AnnotatedMember {
        return KFunctionMember(instance, getter, setter)
    }

}