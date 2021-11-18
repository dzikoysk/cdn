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

package net.dzikoysk.cdn.model

import net.dzikoysk.cdn.CdnSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SectionTest : CdnSpec() {

    private val section = Section(listOf("# comment"), "name", listOf(
        Section(emptyList(), "sub", listOf(
            Entry(emptyList(), "entry", "value"),
            Entry(emptyList(), "int-entry", "7"),
            Entry(emptyList(), "boolKey", "true")
        ))
    ))

    @Test
    fun `should return section name`() {
        assertEquals("name", section.getName())
    }

    @Test
    fun `should return int`() {
        assertEquals(7, section.getInt("sub.int-entry", 0))
    }

    @Test
    fun `should return boolean`() {
        assertTrue(section.getBoolean("sub.boolKey", false))
    }

    @Test
    fun `should return null`() {
        assertTrue(section.get("sub.unknown").isEmpty)
        assertTrue(section.get("unknown.entry").isEmpty)
    }

    @Test
    fun `should contain entry`() {
        assertTrue(section.has("sub"))
        assertFalse(section.has("unknown"))
    }

    @Test
    fun `should return entry of sub section`() {
        assertEquals("value", section.getString("sub.entry", "value"))
    }

    @Test
    fun `should return entry`() {
        assertEquals(section.getEntry("sub.int-entry").get(), section.getSection(0).get().getEntry(1).get())
    }

    @Test
    fun `should append sub element`() {
        val section = Section(emptyList(), "section")
        assertTrue(section.getValue().isEmpty())

        section.append(Entry(emptyList(), "entry", "value"))
        assertEquals(1, section.getValue().size)
    }

}
