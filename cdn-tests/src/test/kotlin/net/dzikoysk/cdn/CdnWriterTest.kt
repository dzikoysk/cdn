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

import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import net.dzikoysk.cdn.module.standard.StandardModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CdnWriterTest : CdnSpec() {

    @Test
    fun `should compose simple entry`() {
        val entry = Entry(listOf("# description"), "key", "value")

        assertEquals(cfg("""
        # description
        key: value
        """), standard.render(entry))
    }

    @Test
    fun `should compose simple section`() {
        val section = Section(listOf("# description"), "section")

        assertEquals(cfg("""
        # description
        section {
        }
        """), standard.render(section))
    }

    @Test
    fun `should compose section with sub section and entry`() {
        val section = Section(listOf("# description"), "section", listOf(
            Section(emptyList(), "sub", listOf(
                Entry(listOf("# entry comment"), "entry", "value")
            ))
        ))

        assertEquals(cfg("""
        # description
        section {
          sub {
            # entry comment
            entry: value
          }
        }
        """), standard.render(section))
    }

    @Test
    fun `should replace simple placeholder`() {
        val entry = Entry(listOf("# \${{placeholder}}"), "key", "value")

        val cdn = Cdn.configure()
            .registerModule(StandardModule())
            .registerPlaceholders(mapOf("placeholder" to "dance with me"))
            .build()

        assertEquals(cfg("""
        # dance with me
        key: value
        """), cdn.render(entry))
    }

}
