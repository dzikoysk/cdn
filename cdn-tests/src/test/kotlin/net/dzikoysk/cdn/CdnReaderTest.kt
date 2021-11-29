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

import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class CdnReaderTest : CdnSpec() {

    @Test
    fun `should return entry`() {
        val result = assertOk(standard.load(Source.of("key: value")))
        val entry = result.getEntry("key").get()
        assertEquals("key", entry.name)
        assertEquals("value", entry.pieceValue)
    }

    @Test
    fun `should return section with comments and entry`() {
        val result = assertOk(standard.load(Source.of("""
        # comment1
        // comment2
        section {
            entry: value
        }
        """)))

        val section = result.getSection("section").orNull
        assertNotNull(section)
        assertEquals(listOf("# comment1", "// comment2"), section!!.description)

        val entry = section.getEntry("entry").orNull
        assertNotNull(entry)
        assertEquals("value", entry!!.pieceValue)
    }

    @Test
    fun `should return nested section`() {
        val result = assertOk(standard.load(Source.of("""
        # c1
        s1 {
            # c2
            some: entry
            s2 {
            }
        }
        """)))

        assertTrue(result.has("s1.s2"))
    }

    @Test
    fun `should skip empty lines`() {
        assertTrue(assertOk(standard.load(Source.empty())).value.isEmpty())
    }

    @Test
    fun `should read quoted key and value`() {
        val result = assertOk(standard.load(Source.of("""
        " key ": " value "
        """)))

        assertEquals(" key ", result.getEntry(" key ").get().name)
        assertEquals(" value ", result.getString(" key ", "value"))
    }

    @Test
    fun `should remove semicolons`() {
        val result = assertOk(standard.load(Source.of("""
        a: b,
        c: d
        """)))

        assertEquals("b", result.getString("a", "value"))
    }

    @Test
    fun `should ignore empty root section`() {
        val result = assertOk(standard.load(Source.of("""
        {
          "a": "b",
          "c": "d"
        }
        """)))

        assertEquals("b", result.getString("a", "value"))
        assertEquals("d", result.getString("c", "value"))
    }

}
