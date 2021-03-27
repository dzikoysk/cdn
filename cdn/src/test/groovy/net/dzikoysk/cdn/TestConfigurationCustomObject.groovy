package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.model.Element
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import net.dzikoysk.cdn.serialization.Composer

import java.lang.reflect.Type

@CompileStatic
class TestConfigurationCustomObject {

    private final String id
    private final int count

    TestConfigurationCustomObject(String id, int count) {
        this.id = id
        this.count = count
    }

    int getCount() {
        return count;
    }

    String getId() {
        return this.id;
    }

    static class CustomObjectComposer implements Composer<TestConfigurationCustomObject> {

        @Override
        TestConfigurationCustomObject deserialize(CdnSettings settings, Element<?> source, Type genericType, TestConfigurationCustomObject defaultValue, boolean entryAsRecord) {
            if (!(source instanceof Section)) {
                throw new IllegalArgumentException('Unsupported element')
            }

            def section = source as Section
            def id = section.getString('id', defaultValue.id)
            def count = section.getInt('count', defaultValue.count)

            return new TestConfigurationCustomObject(id, count)
        }

        @Override
        Element<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, TestConfigurationCustomObject entity) {
            def section = new Section(description, key)
            section.append(new Entry([], 'id', entity.id))
            section.append(new Entry([], 'count', entity.count.toString()))
            return section
        }
    }

}
