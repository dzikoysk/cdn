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

package net.dzikoysk.cdn.features.yaml

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.features.YamlLikeFeature
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class YamlLikeFeatureTest extends CdnSpec {

    private final YamlLikeFeature converter = new YamlLikeFeature()

    @Test
    void 'should convert indentation to brackets' () {
        def source = cfg("""
        key: value
        section:
          subSection:
            subKey: value
          # comment
          key: value
        """)

        assertEquals("""\
        key: value
        section {
          subSection {
            subKey: value
          }
          # comment
          key: value
        }
        """.stripIndent(), converter.convertToCdn(source))
    }


    @Test
    void 'should parse and render yaml like lists' () {
        def source = cfg("""
        list:
          - a
          - b
        """)

        def configuration = yamlLike.load(Source.of(source))

        assertEquals([ "a", "b" ], configuration.getArray('list').get().getList())
        assertEquals(source, yamlLike.render(configuration))
    }

    @Test
    void 'should read lines with brackets' () {
        def result = yamlLike.load(Source.of('''
        section:
            key: {value}
        '''))

        assertEquals '{value}', result.getString('section.key', '')
    }

    @Test
    void 'should use prettier' () {
        def configuration = yamlLike.load(Source.of("""
        section :
          key : value
        """.stripIndent()))

        assertEquals "value", configuration.getString("section.key", 'default')
    }

    @Test
    void 'should compose indentation based source' () {
        assertEquals cfg("""
        section:
          key: value
        """), yamlLike.render(new Object() {
            public @Contextual Object section = new Object() {
                public String key = "value"
            }
        })
    }

}
