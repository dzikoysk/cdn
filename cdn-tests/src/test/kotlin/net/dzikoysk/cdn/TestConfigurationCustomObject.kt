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

import net.dzikoysk.cdn.model.Element
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import net.dzikoysk.cdn.serdes.Composer

import java.lang.reflect.AnnotatedType

class TestConfigurationCustomObject(val id: String, val count: Int)

class CustomObjectComposer : Composer<TestConfigurationCustomObject> {

    override fun deserialize(
        settings: CdnSettings,
        source: Element<*>,
        type: AnnotatedType,
        valueValue: TestConfigurationCustomObject?,
        entryAsRecord: Boolean
    ): TestConfigurationCustomObject {
        if (source !is Section) {
            throw IllegalArgumentException("Unsupported element")
        }

        return TestConfigurationCustomObject(
            id = source.getString("id", valueValue?.id),
            count = source.getInt("count", valueValue?.count ?: 0)
        )
    }

    override fun serialize(
        settings: CdnSettings,
        description: MutableList<String>,
        key: String,
        type: AnnotatedType,
        entity: TestConfigurationCustomObject
    ): Element<*> {
        val section = Section(description, key)
        section.append(Entry(emptyList(), "id", entity.id))
        section.append(Entry(emptyList(), "count", entity.count.toString()))
        return section
    }

}

