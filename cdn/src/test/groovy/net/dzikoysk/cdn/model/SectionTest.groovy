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

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
final class SectionTest extends CdnSpec {

    static final Section SECTION = new Section([ '# comment' ], 'name', [
        new Section([], "sub", [
            new Entry([], "entry", "value"),
            new Entry([], "int-entry", "7"),
            new Entry([], 'boolKey', 'true')
        ])
    ])

    @Test
    void 'should return section name' () {
        assertEquals 'name', SECTION.getName()
    }

    @Test
    void 'should return int' () {
        assertEquals 7, SECTION.getInt('sub.int-entry', 0)
    }

    @Test
    void 'should return boolean' () {
        assertEquals true, SECTION.getBoolean('sub.boolKey', false)
    }

    @Test
    void 'should return null' () {
        assertTrue SECTION.get('sub.unknown').isEmpty()
        assertTrue SECTION.get('unknown.entry').isEmpty()
    }

    @Test
    void 'should contain entry' () {
        assertTrue SECTION.has('sub')
        assertFalse SECTION.has('unknown')
    }

    @Test
    void 'should return entry of sub section' () {
        assertEquals 'value', SECTION.getString('sub.entry', 'default')
    }

    @Test
    void 'should return entry' () {
        assertEquals SECTION.getEntry('sub.int-entry').get(), SECTION.getSection(0).get().getEntry(1).get()
    }

    @Test
    void 'should append sub element' () {
        Section section = new Section([], 'section')
        def elements = section.getValue() as List<Element>
        assertTrue elements.isEmpty()

        elements = section.getValue() as List<Element>
        section.append(new Entry([], 'entry', 'value'))
        assertEquals 1, elements.size()
    }

}
