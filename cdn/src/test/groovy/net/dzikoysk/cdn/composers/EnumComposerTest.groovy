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
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class EnumComposerTest extends CdnSpec {

    enum SomeEnum {
        VAR,
        VAL
    }

    static class ConfigurationWithEnum {

        public SomeEnum enumValue = SomeEnum.VAL

    }

    @Test
    void 'should support enum types' () {
        def source = cfg("""
        enumValue: VAL
        """)

        def result = standard.load(source, ConfigurationWithEnum.class)
        assertEquals SomeEnum.VAL, result.enumValue

        result.enumValue = SomeEnum.VAR

        assertEquals(cfg("""
        enumValue: VAR
        """), standard.render(result))
    }

}
