package net.dzikoysk.cdn.entity;

import net.dzikoysk.cdn.serialization.Composer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomComposer {

    Class<? extends Composer<?>> value();

}
