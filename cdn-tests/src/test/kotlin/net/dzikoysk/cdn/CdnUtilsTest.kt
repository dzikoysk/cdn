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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CdnUtilsTest {

    @Test
    fun `should remove string operators`() {
        assertEquals("test", CdnUtils.destringify("test"))
        assertEquals("test", CdnUtils.destringify(""""test""""))
        assertEquals("test", CdnUtils.destringify("`test`"))
        assertEquals("test", CdnUtils.destringify("'test'"))
    }

    @Test
    fun `should add string operators if needed`() {
        assertEquals(""""test"""", CdnUtils.stringify(""""test""""))
        assertEquals("\"test", CdnUtils.stringify("\"test"))
        assertEquals("test\"", CdnUtils.stringify("test\""))

        assertEquals("\" \"", CdnUtils.stringify(" "))
        assertEquals("\",\"", CdnUtils.stringify(","))
        assertEquals("\"test, \"", CdnUtils.stringify("test, "))
    }

}
