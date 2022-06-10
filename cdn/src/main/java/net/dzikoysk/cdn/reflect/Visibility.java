package net.dzikoysk.cdn.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public enum Visibility {
    PUBLIC(true, Modifier::isPublic),
    PACKAGE_PRIVATE (false, mod -> !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod), PUBLIC),
    PROTECTED(false, Modifier::isProtected, PACKAGE_PRIVATE, PUBLIC),
    PRIVATE(false, Modifier::isPrivate, PROTECTED, PACKAGE_PRIVATE, PUBLIC);

    private final boolean accessible;
    private final Visibility[] included;
    private final Predicate<Integer> predicate;

    private Visibility(boolean accessible, Predicate<Integer> predicate, Visibility... included) {
        this.accessible = accessible;
        this.included = included;
        this.predicate = predicate;
    }

    public boolean isAccessible() {
        return accessible;
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

    public boolean isNotVisible(int modifiers) {
        return !this.isVisible(modifiers);
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
