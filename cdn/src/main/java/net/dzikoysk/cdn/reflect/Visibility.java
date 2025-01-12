package net.dzikoysk.cdn.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public enum Visibility {
    PUBLIC(Modifier::isPublic),
    PACKAGE_PRIVATE(mod -> !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod), PUBLIC),
    PROTECTED(Modifier::isProtected, PACKAGE_PRIVATE, PUBLIC),
    PRIVATE(Modifier::isPrivate, PROTECTED, PACKAGE_PRIVATE, PUBLIC);

    private final Visibility[] included;
    private final Predicate<Integer> predicate;

    Visibility(Predicate<Integer> predicate, Visibility... included) {
        this.included = included;
        this.predicate = predicate;
    }

    public boolean isVisible(int modifiers) {
        if (predicate.test(modifiers)) {
            return true;
        }

        for (Visibility visibility : included) {
            if (visibility.predicate.test(modifiers)) {
                return true;
            }
        }

        return false;
    }

    public boolean isVisible(Member member) {
        return this.isVisible(member.getModifiers());
    }

    public boolean isVisible(Class<?> type) {
        return this.isVisible(type.getModifiers());
    }

    public static Visibility forMember(Member member) {
        for (Visibility visibility : values()) {
            if (visibility.isVisible(member)) {
                return visibility;
            }
        }

        throw new IllegalStateException();
    }

}
