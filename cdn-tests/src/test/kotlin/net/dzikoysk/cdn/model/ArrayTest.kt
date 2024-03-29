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
import org.junit.jupiter.api.Test

class ArrayTest : CdnSpec() {

    @Test
    fun `should remove list operators`() {
        val section = Array(emptyList(), "list", listOf(
            Piece("- val1 "),
            Piece("- \" val2 \"")
        ))

        assertEquals(listOf("val1", " val2 "), section.list)
    }

    @Test
    fun `should return list`() {
        val section = Section(emptyList(), "section", listOf(
                Array(emptyList(), "list", listOf(
                    Piece("record 1"),
                    Piece("- record 2 : with semicolon")
                ))
        ))

        assertEquals(
            listOf("record 1", "- record 2 : with semicolon"),
            section.getList("list", listOf("value"))
        )
    }


}
