package net.dzikoysk.cdn.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class GenericUtils {

    private GenericUtils() {}

    public static Type[] getGenericTypes(Field field) {
        return getGenericTypes(field.getGenericType());
    }

    public static Type[] getGenericTypes(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }

        throw new IllegalArgumentException("Missing generic signature");
    }

    public static Class<?>[] getGenericClasses(Field field) {
        return getGenericClasses(field.getGenericType());
    }

    public static Class<?>[] getGenericClasses(Type type) {
        return Arrays.stream(getGenericTypes(type))
                .map(GenericUtils::toClass)
                .toArray(Class[]::new);
    }

    public static Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }

        try {
            return Class.forName(type.getTypeName());
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new IllegalArgumentException("Cannot find generic type " + type);
        }
    }

}
