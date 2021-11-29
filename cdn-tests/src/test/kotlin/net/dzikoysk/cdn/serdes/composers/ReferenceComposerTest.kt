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
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk
import panda.std.reactive.mutableReference
import panda.std.reactive.reference

class ReferenceComposerTest : CdnSpec() {

    class ReferenceConfiguration {
        val reference = reference("default-value")
        val mutableReference = mutableReference("mutable-default-value")
    }

    @Test
    fun `should support references`() {
        val configuration = ReferenceConfiguration()
        var subscriberCalled = false
        configuration.mutableReference.subscribe { subscriberCalled = true }

        standard.load(
                Source.of("""
                reference: value
                mutableReference: mutable-value
                """),
                configuration
        )

        assertEquals("value", configuration.reference.get())
        assertEquals("mutable-value", configuration.mutableReference.get())
        assertTrue(subscriberCalled)

        assertEquals(cfg("""
        reference: value
        mutableReference: mutable-value
        """), assertOk(standard.render(configuration)))
    }

}
