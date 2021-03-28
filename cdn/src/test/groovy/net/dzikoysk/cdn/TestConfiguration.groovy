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
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.entity.SectionLink
import net.dzikoysk.cdn.entity.CustomComposer

@CompileStatic
class TestConfiguration {

    @Description('# Root entry description')
    public String rootEntry = "default value"

    @Description(['', '// Section description'])
    @SectionLink
    public SectionConfiguration section = new SectionConfiguration()

    static class SectionConfiguration {

        @Description('# Random value')
        public Integer subEntry = -1

        @Description('# List description')
        public List<String> list = [ 'record', 'record : with : semicolons' ]

        @Description('# Custom object')
        @CustomComposer(TestConfigurationCustomObject.CustomObjectComposer.class)
        public TestConfigurationCustomObject custom = new TestConfigurationCustomObject('rtx', 3070)

    }

}