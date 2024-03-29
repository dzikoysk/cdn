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

package net.dzikoysk.cdn.serdes.composers

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.serdes.composers.EnumComposerTest.SomeEnum.VAL
import net.dzikoysk.cdn.serdes.composers.EnumComposerTest.SomeEnum.VAR
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class EnumComposerTest : CdnSpec() {

    enum class SomeEnum {
        VAR,
        VAL
    }

    class ConfigurationWithEnum {
        var enumValue = VAL
    }

    @Test
    fun `should support enum types`() {
        val source = cfg("""
        enumValue: val
        """)

        val result = assertOk(standard.loadAs<ConfigurationWithEnum>(Source.of(source)))
        assertEquals(VAL, result.enumValue)

        result.enumValue = VAR
        assertEquals(cfg("enumValue: VAR"), assertOk(standard.render(result)))
    }

    class ConfigurationWithListOfEnums {
        var elements = listOf(VAL, VAR)
    }

    @Test
    fun `should support list of enums`() {
        val source = cfg("""
        elements:
          - "VAL"
          - "VAR"
        """)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithListOfEnums>(Source.of(source)))
        assertEquals(listOf(VAL, VAR), configuration.elements)
        assertEquals(source, assertOk(yamlLike.render(configuration)))
    }

    class ConfigurationWithEnumMap {
        var enums = mapOf(VAR to VAL)
    }

    @Test
    fun `should support maps with enums`() {
        val source = cfg("""
        enums:
          VAL: "VAR"
        """)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithEnumMap>(Source.of(source)))
        assertEquals(mapOf(VAL to VAR), configuration.enums)
        assertEquals(source, assertOk(yamlLike.render(configuration)))
    }

}
