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

import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.entity.CustomComposer
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.entity.Exclude

class TestConfiguration {

    @Description("# Root entry description")
    var rootEntry = "value value"

    var strangeValue = "\""

    @Description("", "// Section description")
    var section = SectionConfiguration()

    @Description("# Skip")
    var skip: String? = null

    @Contextual
    open class ParentSectionConfiguration {

        @Description("# List description")
        var list = listOf("record", "record : with : semicolons")

        @Description("# Null")
        var nullable: String? = null

        @Description("# Custom object")
        @CustomComposer(CustomObjectComposer::class)
        var custom = TestConfigurationCustomObject("rtx", 3070)

    }

    @Contextual
    class SectionConfiguration : ParentSectionConfiguration() {

        @Description("# Random value")
        var subEntry = -1

        @Description("# Transient fields should be ignored")
        @Exclude
        var shouldBeIgnored = Object()

    }

}