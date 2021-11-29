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
        var enumValue = SomeEnum.VAL
    }

    @Test
    fun `should support enum types`() {
        val source = cfg("""
        enumValue: val
        """)

        val result = assertOk(standard.loadAs<ConfigurationWithEnum>(Source.of(source)))
        assertEquals(SomeEnum.VAL, result.enumValue)

        result.enumValue = SomeEnum.VAR
        assertEquals(cfg("enumValue: VAR"), assertOk(standard.render(result)))
    }

}
