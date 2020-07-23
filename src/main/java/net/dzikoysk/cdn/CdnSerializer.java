package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;

import java.lang.reflect.Field;

final class CdnSerializer {

    private final CDN cdn;

    CdnSerializer(CDN cdn) {
        this.cdn = cdn;
    }

    public Configuration serialize(Object entity) {
        Configuration root = new Configuration();
        serialize(root, entity);
        return root;
    }

    private void serialize(Section root, Object entity) {
        Class<?> scheme = entity.getClass();

        for (Field field : scheme.getDeclaredFields()) {
            if (field.isAnnotationPresent(SectionLink.class)) {

            }
        }
    }

}
