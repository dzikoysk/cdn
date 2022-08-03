package net.dzikoysk.cdn.serdes;

import java.lang.annotation.Annotation;
import java.util.List;

public interface DescriptionProvider<A extends Annotation> {

    List<String> getDescription(A annotation);

}
