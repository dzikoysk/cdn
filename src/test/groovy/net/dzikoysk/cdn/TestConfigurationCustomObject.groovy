package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.ConfigurationElement
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import net.dzikoysk.cdn.serialization.Composer

class TestConfigurationCustomObject {

    private String id
    private int count

    TestConfigurationCustomObject(String id, int count) {
        this.id = id
        this.count = count
    }

    static class CustomObjectComposer implements Composer<TestConfigurationCustomObject> {

        @Override
        TestConfigurationCustomObject deserialize(ConfigurationElement<?> source) {
            if (!(source instanceof Section)) {
                throw new IllegalArgumentException('Unsupported element')
            }

            def section = source as Section
            def id = section.getString('id')
            def count = section.getInt('count')

            return new TestConfigurationCustomObject(id, count)
        }

        @Override
        ConfigurationElement<?> serialize(String key, TestConfigurationCustomObject entity, List<String> description) {
            def section = new Section(key, description)
            section.append(Entry.ofPair('id', entity.id, []))
            section.append(Entry.ofPair('count', entity.count, []))
            return section;
        }
    }

}
