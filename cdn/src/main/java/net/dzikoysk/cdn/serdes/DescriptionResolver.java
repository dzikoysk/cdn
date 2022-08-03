package net.dzikoysk.cdn.serdes;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class DescriptionResolver<A extends Annotation> {

    private final Class<A> annotationType;
    private final DescriptionProvider<A> descriptionProvider;

    public DescriptionResolver(Class<A> annotationType, DescriptionProvider<A> descriptionProvider) {
        this.annotationType = annotationType;
        this.descriptionProvider = descriptionProvider;
    }

    Class<A> getAnnotationType() {
        return annotationType;
    }

    List<String> getDescription(Annotation annotation) {
        if (!annotationType.equals(annotation.annotationType())) {
            return Collections.emptyList();
        }

        return descriptionProvider.getDescription(annotationType.cast(annotation));
    }


}
