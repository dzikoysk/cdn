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
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CdnWriterTest extends CdnSpec{

    @Test
    void 'should compose simple entry' () {
        def entry = new Entry(['# description' ], 'key', 'value')

        assertEquals cfg("""
        # description
        key: value
        """), standard.render(entry)
    }

    @Test
    void 'should compose simple section' () {
        def section = new Section(['# description' ], 'section')

        assertEquals cfg("""
        # description
        section {
        }
        """), standard.render(section)
    }

    @Test
    void 'should compose section with sub section and entry' () {
        def section = new Section(['# description' ], 'section', [
            new Section([], 'sub', [
                new Entry(['# entry comment'], 'entry', 'value')
            ])
        ])

        assertEquals cfg("""
        # description
        section {
          sub {
            # entry comment
            entry: value
          }
        }
        """), standard.render(section)
    }

    @Test
    void 'should replace simple placeholder' () {
        def entry = new Entry([ '# ${{placeholder}}' ], 'key', 'value')

        def cdn = Cdn.configure()
            .registerPlaceholders([ 'placeholder': 'dance with me' ])
            .build()

        assertEquals cfg("""
        # dance with me
        key: value
        """), cdn.render(entry)
    }

}
