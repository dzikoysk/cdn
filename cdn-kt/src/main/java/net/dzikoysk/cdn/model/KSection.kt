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

open class KSection(val section: Section) : KNamedElement<MutableList<out Element<*>>>(section) {

    fun <E : KNamedElement<*>> append(element: E): E {
        section.append(element.namedElement)
        return element
    }

    fun has(key: String): Boolean = section.has(key)

    fun size(): Int = section.size()

    fun get(index: Int): KElement<*>? = section.get(index).orNull?.let { KElement(it) }

    fun get(key: String): KElement<*>? = section.get(key).orNull?.let { KElement(it) }

    fun getList(key: String): List<String>? = section.getList(key).orNull

    fun getBoolean(key: String): Boolean? = section.getBoolean(key).orNull

    fun getInt(key: String): Int? = section.getInt(key).orNull

    fun getString(key: String): String? = section.getString(key).orNull

    fun getEntry(index: Int): KEntry? = section.getEntry(index).orNull?.let { KEntry(it) }

    fun getEntry(key: String): KEntry? = section.getEntry(key).orNull?.let { KEntry(it) }

    fun getSection(index: Int): KSection? = section.getSection(index).orNull?.let { KSection(it) }

    fun getSection(key: String): KSection? = section.getSection(key).orNull?.let { KSection(it) }

}