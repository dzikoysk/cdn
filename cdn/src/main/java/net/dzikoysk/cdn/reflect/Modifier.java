package net.dzikoysk.cdn.reflect;

import java.lang.reflect.Member;
import java.util.function.Predicate;

public final class Modifier {

    public static final Modifier PUBLIC           = of(modifiers -> (modifiers & 0x00000001) != 0);
    public static final Modifier PRIVATE          = of(modifiers -> (modifiers & 0x00000002) != 0);
    public static final Modifier PROTECTED        = of(modifiers -> (modifiers & 0x00000004) != 0);
    public static final Modifier PACKAGE_PRIVATE  = of(mod -> PUBLIC.hasNot(mod) && PRIVATE.hasNot(mod) && PROTECTED.hasNot(mod));
    public static final Modifier STATIC           = of(modifiers -> (modifiers & 0x00000008) != 0);
    public static final Modifier FINAL            = of(modifiers -> (modifiers & 0x00000010) != 0);
    public static final Modifier SYNCHRONIZED     = of(modifiers -> (modifiers & 0x00000020) != 0);
    public static final Modifier VOLATILE         = of(modifiers -> (modifiers & 0x00000040) != 0);
    public static final Modifier TRANSIENT        = of(modifiers -> (modifiers & 0x00000080) != 0);
    public static final Modifier NATIVE           = of(modifiers -> (modifiers & 0x00000100) != 0);
    public static final Modifier INTERFACE        = of(modifiers -> (modifiers & 0x00000200) != 0);
    public static final Modifier ABSTRACT         = of(modifiers -> (modifiers & 0x00000400) != 0);
    public static final Modifier STRICT           = of(modifiers -> (modifiers & 0x00000800) != 0);

    private final Predicate<Integer> predicate;

    private Modifier(Predicate<Integer> predicate) {
        this.predicate = predicate;
    }

    public boolean has(int modifiers) {
        return this.predicate.test(modifiers);
    }

    public boolean has(Member member) {
        return this.has(member.getModifiers());
    }

    public boolean has(Class<?> type) {
        return this.has(type.getModifiers());
    }

    public boolean hasNot(int modifiers) {
        return !this.has(modifiers);
    }

    private static Modifier of(Predicate<Integer> predicate) {
        return new Modifier(predicate);
    }

}
