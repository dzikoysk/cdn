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

package net.dzikoysk.cdn.composers

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnFactory
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class ListComposerTest {

    static class Configuration {
    
        public List<String> list = Collections.emptyList()
        
    }

    @Test
    void 'should load empty list' () {
        def source = """
        list: []
        """.stripIndent().trim()

        def configuration = CdnFactory.createYamlLike().load(source, Configuration.class)
        assertEquals(Collections.emptyList(), configuration.list)
        assertEquals(source, CdnFactory.createYamlLike().render(configuration))
    }

    @Test
    void 'should load list' () {
        def source = """
        list [
          a:1
          b:2
        ]
        """.stripIndent().trim()

        def list = [
            "a:1",
            "b:2"
        ]

        def configuration = CdnFactory.createStandard().load(source, Configuration.class)
        assertEquals(list, configuration.list)
        assertEquals(source, CdnFactory.createStandard().render(configuration))
    }

    @Test
    void 'should load object based list' () {
        def source = """
        list {
          a:1
          b: 2
        }
        """.stripIndent().trim()

        def configuration = CdnFactory.createStandard().load(source, Configuration.class)
        assertEquals([ "a:1", "b: 2" ], configuration.list)

        def formattedSource = """
        list [
          a:1
          b: 2
        ]
        """.stripIndent().trim()

        assertEquals(formattedSource, CdnFactory.createStandard().render(configuration))
    }

}
