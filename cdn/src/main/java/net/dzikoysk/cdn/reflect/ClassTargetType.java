package net.dzikoysk.cdn.reflect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ClassTargetType implements TargetType {

    private final Class<?> type;
    private final MemberResolver resolver;

    public ClassTargetType(Class<?> type, MemberResolver resolver) {
        this.type = type;
        this.resolver = resolver;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return type.getAnnotation(annotation);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return annotation.isAnnotationPresent(annotation);
    }

    public List<AnnotatedMember> getAnnotatedMembers() {
        List<AnnotatedMember> members = new ArrayList<>();
        members.addAll(resolver.getFields(type));
        members.addAll(resolver.getProperties(type));
        return members;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

}
