package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.DeserializationHandler;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.lang.reflect.Field;

public final class CdnDeserializer<T> {

    private final CDN cdn;

    CdnDeserializer(CDN cdn) {
        this.cdn = cdn;
    }

    protected T deserialize(Class<T> scheme, Configuration content) throws Exception {
        T instance = scheme.getConstructor().newInstance();
        deserialize(instance, content);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private void deserialize(Object instance, Section root) throws Exception {
        CdnSettings settings = cdn.getSettings();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            if (settings.getDeserializers().get(field.getType()) == null && !field.isAnnotationPresent(SectionLink.class) && !field.isAnnotationPresent(CustomComposer.class)) {
                throw new UnsupportedOperationException("Unsupported type, missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "'");
            }

            Option<ConfigurationElement<?>> elementValue = root.get(field.getName());

            if (elementValue.isEmpty()) {
                continue;
            }

            ConfigurationElement<?> element = elementValue.get();
            Object defaultValue = field.get(instance);

            if (field.isAnnotationPresent(SectionLink.class)) {
                deserialize(field.get(instance), (Section) element);
                continue;
            }

            deserialize(cdn.getSettings(), instance, field, defaultValue, element);
        }
    }

    private void deserialize(CdnSettings settings, Object instance, Field field, Object defaultValue, ConfigurationElement<?> element) throws Exception {
        Deserializer<Object> deserializer = getDeserializer(settings, field.getType(), field);
        Object value = deserializer.deserialize(settings, element, field.getGenericType(), defaultValue, false);
        field.set(instance, value);
    }

    public static Deserializer<Object> getDeserializer(CdnSettings settings, Class<?> type, @Nullable Field field) throws Exception {
        Deserializer<Object> deserializer;

        if (field != null && field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            deserializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            deserializer = settings.getDeserializers().get(type);
        }

        if (deserializer == null) {
            throw new UnsupportedOperationException(
                    "Missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "' type. Available deserializers: " +
                            settings.getDeserializers().keySet().toString()
            );
        }

        return deserializer;
    }

}
