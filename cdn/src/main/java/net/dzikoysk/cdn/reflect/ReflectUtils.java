package net.dzikoysk.cdn.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectUtils {

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
        Class<?> superclass = type.getSuperclass();

        if (!superclass.equals(Object.class)) {
            fields.addAll(getAllFields(superclass));
        }

        return fields;
    }

}
