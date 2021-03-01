package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.Exclude;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class CdnUtils {

    private CdnUtils() {}

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
                .map(CdnUtils::toClass)
                .toArray(Class[]::new);
    }

    public static Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }

        try {
            return Class.forName(type.getTypeName());
        } catch (ClassNotFoundException classNotFoundException) {
            throw new IllegalArgumentException("Cannot find generic type " + type);
        }
    }

    public static String destringify(String value) {
        for (String operator : CdnConstants.STRING_OPERATORS) {
            if (value.startsWith(operator) && value.endsWith(operator)) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    public static String stringify(String value) {
        if (!value.startsWith("\"") && !value.endsWith("\"")) {
            if (value.trim().length() != value.length() || value.endsWith(",")) {
                return "\"" + value + "\"";
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
