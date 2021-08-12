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
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class ListComposerTest extends CdnSpec {

    @CompileStatic
    static class ConfigurationWithEmptyList {

        public List<String> list = Collections.emptyList()

    }

    @CompileStatic
    static class ConfigurationWithListOfValues {

        public List<Element> elements = [ new Element(), new Element() ]

        @Contextual
        static class Element {

            public String name = "default value"

            @Override
            boolean equals(Object obj) {
                return Objects.equals(name, ((Element) obj).name)
            }
        }

    }

    @Test
    void 'should load list' () {
        def source = cfg("""
        list [
          a:1
          b:2
        ]
        """)

        def configuration = standard.load(source, ConfigurationWithEmptyList.class)
        assertEquals([ "a:1", "b:2" ], configuration.list)

        def expectedSource = cfg("""
        list [
          a:1
          b:2
        ]
        """)

        assertEquals(expectedSource, standard.render(configuration))
    }

    @Test
    void 'should load object based list' () {
        def configuration = standard.load("", ConfigurationWithListOfValues.class)

        def formattedSource = cfg("""
        elements [
          {
            name: default value
          }
          {
            name: default value
          }
        ]
        """)

        assertEquals(formattedSource, standard.render(configuration))
    }

}
