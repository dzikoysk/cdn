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
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class SimpleComposerTest : CdnSpec() {

    class Configuration {
        @Description("# Separator with space")
        var separator = ", "
        var singleQuote = "\""
        var empty = ""
    }

    // Make sure that these tricky values are properly handled by CDN
    // ~ https://github.com/dzikoysk/cdn/issues/40 - comma operator
    // ~ https://github.com/dzikoysk/cdn/issues/43 - empty strings
    @Test
    fun `should properly serialize and deserialize empty value, value with comma and values with spaces`() {
        var currentSource = assertOk(yamlLike.render(Configuration()))

        repeat(3) {
            println(currentSource)
            val configuration = assertOk(yamlLike.loadAs<Configuration>(Source.of(currentSource)))
            currentSource = assertOk(yamlLike.render(configuration))
        }
    }

}
