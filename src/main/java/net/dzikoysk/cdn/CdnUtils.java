package net.dzikoysk.cdn;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class CdnUtils {

    private CdnUtils() {}

    static Class<?> getGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type genericType = parameterizedType.getActualTypeArguments()[0];

            try {
                return Class.forName(genericType.getTypeName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot find generic type " + genericType);
            }
        }

        throw new IllegalArgumentException("Missing generic signature");
    }

}
