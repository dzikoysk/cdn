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