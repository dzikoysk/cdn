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

package net.dzikoysk.cdn.module.yaml

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.TestConfiguration
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class YamlLikeSerializerDeserializerTest : CdnSpec() {

    @Test
    fun `should serialize entity with indentation based formatting`() {
        assertEquals(cfg("""
        # Root entry description
        rootEntry: "value value"

        # Section description
        section:
          # Random value
          subEntry: "-1"
          # List description
          list:
            - "record"
            - "record : with : semicolons"
          # Custom object
          custom:
            id: "rtx"
            count: "3070"
        
        # Class
        clazz: "java.lang.String"
        """), assertOk(yamlLike.render(TestConfiguration())))
    }

    @Test
    fun `should deserialize source into scheme`() {
        val configuration = assertOk(yamlLike.load(Source.of("""
        # Root entry description
        rootEntry:  custom value!
        
        # Section description
        section:
          # Random value
          subEntry: 7
          # List description
          list:
            - record :
            - record : with : semicolons
          # Custom object
          custom:
            id: rtx
            count: 3080
        
        # Class
        clazz: java.lang.String
        """), TestConfiguration::class.java))

        assertEquals("custom value!", configuration.rootEntry)
        assertEquals(7, configuration.section.subEntry)
        assertEquals(listOf("record :", "record : with : semicolons"), configuration.section.list)
        assertEquals("rtx", configuration.section.custom.id)
        assertEquals(3080, configuration.section.custom.count)
    }

    @Test
    fun `should render same string after second render`() {
        val firstRender = yamlLike.render(TestConfiguration())
        assertOk(firstRender)

        val firstLoad = yamlLike.load(Source.of(firstRender.get()), TestConfiguration::class.java)
        assertOk(firstLoad)

        val secondRender = yamlLike.render(firstLoad.get())
        assertEquals(firstRender, secondRender)

        val secondLoad = yamlLike.load(Source.of(secondRender.get()), TestConfiguration::class.java)
        assertOk(secondLoad)
    }

}
