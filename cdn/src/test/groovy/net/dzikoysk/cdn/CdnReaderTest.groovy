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

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static net.dzikoysk.cdn.shared.source.Source.empty
import static net.dzikoysk.cdn.shared.source.Source.of
import static org.junit.jupiter.api.Assertions.*

@CompileStatic
final class CdnReaderTest extends CdnSpec {

    @Test
    void 'should return entry' () {
        def result = standard.load(of('key: value'))
        def entry = result.getEntry('key').get()
        assertEquals 'key', entry.getName()
        assertEquals 'value', entry.getUnitValue()
    }

    @Test
    void 'should return section with comments and entry' () {
        def result = standard.load(of("""
        # comment1
        // comment2
        section {
            entry: value
        }
        """))

        def section = result.getSection('section').get()
        assertNotNull section
        assertEquals([ '# comment1', '// comment2' ], section.getDescription())

        def entry = section.getEntry('entry').get()
        assertNotNull entry
        assertEquals 'value', entry.getUnitValue()
    }

    @Test
    void 'should return nested section' () {
        def result = standard.load(of('''
        # c1
        s1 {
            # c2
            some: entry
            s2 {
            }
        }
        '''))

        assertTrue result.has('s1.s2')
    }

    @Test
    void 'should skip empty lines' () {
        assertTrue standard.load(empty()).getValue().isEmpty()
    }

    @Test
    void 'should read quoted key and value' () {
        def result = standard.load(of('''
        " key ": " value "
        '''))

        assertEquals ' key ', result.getEntry(' key ').get().getName()
        assertEquals ' value ', result.getString(' key ', 'default')
    }

    @Test
    void 'should remove semicolons' () {
        def result = standard.load(of("""
        a: b,
        c: d
        """))

        assertEquals 'b', result.getString('a', 'default')
    }

    @Test
    void 'should ignore empty root section' () {
        def result = standard.load(of("""
        {
          "a": "b",
          "c": "d"
        }
        """))

        assertEquals 'b', result.getString('a', 'default')
        assertEquals 'd', result.getString('c', 'default')
    }

}
