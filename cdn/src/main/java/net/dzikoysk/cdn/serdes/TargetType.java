package net.dzikoysk.cdn.serdes;

import net.dzikoysk.cdn.CdnUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

public interface TargetType {

    default TargetType[] getAnnotatedActualTypeArguments() {
        return new TargetType[0];
    }

    <A extends Annotation> A getAnnotation(Class<A> annotation);

    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    Class<?> getType();

    class AnnotatedTargetType implements TargetType {

        private final AnnotatedType annotatedType;

        public AnnotatedTargetType(AnnotatedType annotatedType) {
            this.annotatedType = annotatedType;
        }

        @Override
        public TargetType[] getAnnotatedActualTypeArguments() {
            if (annotatedType instanceof AnnotatedParameterizedType) {
                AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
                return Arrays.stream(annotatedParameterizedType.getAnnotatedActualTypeArguments())
                        .map(AnnotatedTargetType::new)
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
        public Class<?> getType() {
            return CdnUtils.toClass(annotatedType.getType());
        }

    }

    class ClassTargetType implements TargetType {

        private final Class<?> type;

        public ClassTargetType(Class<?> type) {
            this.type = type;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotation) {
            return type.getAnnotation(annotation);
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
            return annotation.isAnnotationPresent(annotation);
        }

        @Override
        public Class<?> getType() {
            return type;
        }

    }

}
