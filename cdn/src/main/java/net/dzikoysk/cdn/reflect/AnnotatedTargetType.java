package net.dzikoysk.cdn.reflect;

import net.dzikoysk.cdn.CdnUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.List;

public class AnnotatedTargetType implements TargetType {

    private final AnnotatedType annotatedType;
    private final MemberResolver resolver;

    public AnnotatedTargetType(AnnotatedType annotatedType, MemberResolver resolver) {
        this.annotatedType = annotatedType;
        this.resolver = resolver;
    }

    @Override
    public TargetType[] getAnnotatedActualTypeArguments() {
        if (annotatedType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
            return Arrays.stream(annotatedParameterizedType.getAnnotatedActualTypeArguments())
                    .map(type -> new AnnotatedTargetType(type, resolver))
                    .toArray(TargetType[]::new);
        }

        return new TargetType[0];
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return annotatedType.getAnnotation(annotation);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return annotatedType.isAnnotationPresent(annotation);
    }

    @Override
    public List<AnnotatedMember> getAnnotatedMembers() {
        return new ClassTargetType(getType(), resolver).getAnnotatedMembers();
    }

    @Override
    public Class<?> getType() {
        return CdnUtils.toClass(annotatedType.getType());
    }

}
