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
import kotlin.reflect.KClass

class KCdn(private val cdn: Cdn) {

    fun parse(source: String): KConfiguration = KConfiguration(cdn.load { source } )

    fun <T : Any> parse(scheme: KClass<T>, source: String): T = cdn.load( { source } , scheme.java)

    inline fun <reified T : Any> parseAs(source: String): T = this.parse(T::class, source)

    fun render(element: KNamedElement<*>): String = cdn.render(element.namedElement)

    fun render(entity: Any): String = cdn.render(entity)

    companion object {

        fun configure(): CdnSettings = CdnSettings()

    }

}