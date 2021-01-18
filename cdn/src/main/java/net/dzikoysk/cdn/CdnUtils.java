package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.Exclude;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class CdnUtils {

    private CdnUtils() {}

    static Class<?> getGenericType(Field field) {
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type genericType = parameterizedType.getActualTypeArguments()[0];

            try {
                return Class.forName(genericType.getTypeName());
            } catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalArgumentException("Cannot find generic type " + genericType);
            }
        }

        throw new IllegalArgumentException("Missing generic signature");
    }

    public static String destringify(String value) {
        for (String operator : CdnConstants.STRING_OPERATORS) {
            if (value.startsWith(operator) && value.endsWith(operator)) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    static boolean isIgnored(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            return true;
        }

        // ignore Groovy properties
        if (field.getName().startsWith("__$") || field.getName().startsWith("$")) {
            return true;
        }

        return field.isAnnotationPresent(Exclude.class);
    }

}
