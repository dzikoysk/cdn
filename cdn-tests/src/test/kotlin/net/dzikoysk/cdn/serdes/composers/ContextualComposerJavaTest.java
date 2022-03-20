package net.dzikoysk.cdn.serdes.composers;

import net.dzikoysk.cdn.entity.Contextual;

public final class ContextualComposerJavaTest {

    public static final class StandardConfiguration {
        public  Section section = new Section();

        @Contextual
        public static final class Section {
            public int primitive = 1;
        }
    }

}
