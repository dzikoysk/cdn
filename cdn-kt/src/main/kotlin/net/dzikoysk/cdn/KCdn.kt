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

import net.dzikoysk.cdn.model.KConfiguration
import net.dzikoysk.cdn.model.KNamedElement
import net.dzikoysk.cdn.source.Source
import kotlin.reflect.KClass

class KCdn(val cdn: Cdn) {

    companion object {
        fun configure(): CdnSettings = CdnSettings()
    }

    fun parse(source: Source): KConfiguration = KConfiguration(cdn.load(source))

    fun <T : Any> parse(source: Source, template: KClass<T>): T = cdn.load(source, template.java)

    fun <T : Any> parse(source: Source, instance: T): T = cdn.load(source, instance)

    inline fun <reified T : Any> parseAs(source: Source): T = this.parse(source, T::class)

    fun render(element: KNamedElement<*>): String = cdn.render(element.namedElement)

    fun render(entity: Any): String = cdn.render(entity)

}

fun Cdn.toKotlinWrapper(): KCdn = KCdn(this)