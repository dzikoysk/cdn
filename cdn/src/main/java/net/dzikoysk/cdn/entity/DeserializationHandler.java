package net.dzikoysk.cdn.entity;

public interface DeserializationHandler<T> {

    T handle(T instance);

}
