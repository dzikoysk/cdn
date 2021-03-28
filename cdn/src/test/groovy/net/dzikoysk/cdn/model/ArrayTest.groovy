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
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class ArrayTest {

    @Test
    void 'should remove list operators' () {
        Array section = new Array([], 'list', [
                new Unit('- val1 '),
                new Unit('- " val2 "')
        ])

        assertEquals(['val1', ' val2 '], section.getList())
    }

    @Test
    void 'should return list' () {
        Section section = new Section([], 'section', [
                new Array([], 'list', [
                        new Unit('record 1'),
                        new Unit('- record 2 : with semicolon')
                ])
        ])

        assertEquals(['record 1', '- record 2 : with semicolon'], section.getList('list', [ 'default' ]))
    }


}
