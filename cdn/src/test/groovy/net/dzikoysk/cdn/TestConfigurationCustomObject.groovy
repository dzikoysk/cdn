/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        TestConfigurationCustomObject deserialize(CdnSettings settings, Element<?> source, Type type, TestConfigurationCustomObject defaultValue, boolean entryAsRecord) {
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
