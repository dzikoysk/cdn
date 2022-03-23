package net.dzikoysk.cdn.reflect;

import java.lang.annotation.Annotation;
import java.util.List;

public interface TargetType {

    default TargetType[] getAnnotatedActualTypeArguments() {
        return new TargetType[0];
    }

    <A extends Annotation> A getAnnotation(Class<A> annotation);

    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    List<AnnotatedMember> getAnnotatedMembers();

    Class<?> getType();

}
