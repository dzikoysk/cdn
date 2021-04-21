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

package net.dzikoysk.cdn.features.json

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.features.JsonFeature
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class JsonFeatureTest extends CdnSpec {

    private final JsonFeature converter = new JsonFeature()

    @Test
    void 'should convert json like format to cdn' () {
        def source = converter.convertToCdn(cfg('{"list":[{"key":"value"},{"key":"value"}]}'))

        assertEquals(cfg("""
        {
          "list": [
            {
              "key": "value"
            },
            {
              "key": "value"
            }
          ]
        }
        """), source)

        def configuration = json.load(source)
        def list = configuration.getSection('list').get()
        assertEquals 2, list.size()
    }

    @Test
    void 'should convert json like list' () {
        def source = converter.convertToCdn('{ "list": [ "a", "b", "c" ] }')
        assertEquals([ 'a', 'b', 'c' ], standard.load(source).getList('list', ['default' ]))
    }

    @Test
    void 'should escape operators in strings' () {
        def source = converter.convertToCdn(cfg('{"key":"{value}"}'))

        assertEquals(cfg("""
        {
          "key": "{value}"
        }
        """), source)
    }

    @Test
    void 'should parse json' () {
        def source = '{ "object": { "key": "value" }, "array": [ "1", "2" ] }'

        def cdn = cfg("""
        object: {
          key: value
        }
        array: [
          1
          2
        ]
        """)

        def jsonResult = json.load(source)
        def standardResult = standard.load(cdn)

        assertEquals standardResult.getString('object.key', 'defaultCDN'), jsonResult.getString('object.key', 'defaultJSON')
        assertEquals standardResult.getList('array', [ 'defaultCDN' ]), jsonResult.getList('array', [ 'defaultJSON' ])
    }

}
