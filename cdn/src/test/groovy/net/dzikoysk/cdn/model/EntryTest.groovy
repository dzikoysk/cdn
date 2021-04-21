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

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnSpec
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class EntryTest extends CdnSpec {

    static final Entry ENTRY = new Entry(['// entry-comment' ], 'entry-name', 'entry-value')

    @Test
    void 'should return entry name' () {
        assertEquals 'entry-name', ENTRY.getName()
    }

    @Test
    void 'should return entry value' () {
        assertEquals 'entry-value', ENTRY.getUnitValue()
    }

    @Test
    void 'should return entry comments' () {
        assertEquals([ '// entry-comment' ], ENTRY.getDescription())
    }

    @Test
    void 'should return entry record' () {
        assertEquals 'entry-name: entry-value', ENTRY.getRecord()
    }

}
