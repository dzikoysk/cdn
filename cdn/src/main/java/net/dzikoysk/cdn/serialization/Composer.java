package net.dzikoysk.cdn.serialization;

/**
 * The composer represents implementation of both: serializer and deserializer interfaces.
 *
 * @param <T> type of value to serialize/deserialize
 */
public interface Composer<T> extends Serializer<T>, Deserializer<T> { }
