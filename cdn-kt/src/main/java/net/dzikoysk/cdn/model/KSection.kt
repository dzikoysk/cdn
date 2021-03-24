package net.dzikoysk.cdn.model

open class KSection(val section: Section) : KConfigurationElement<MutableList<out ConfigurationElement<*>>>(section) {

    fun <E : KConfigurationElement<*>> append(element: E): E {
        section.append(element.configurationElement)
        return element
    }

    fun has(key: String): Boolean = section.has(key)

    fun size(): Int = section.size()

    fun get(index: Int): KConfigurationElement<*>? = section.get(index).orNull?.let { KConfigurationElement(it) }

    fun get(key: String): KConfigurationElement<*>? = section.get(key).orNull?.let { KConfigurationElement(it) }

    fun getList(key: String): List<String>? = section.getList(key).orNull

    fun getList(): List<String> = section.list

    fun getBoolean(key: String): Boolean? = section.getBoolean(key).orNull

    fun getInt(key: String): Int? = section.getInt(key).orNull

    fun getString(key: String): String? = section.getString(key).orNull

    fun getEntry(index: Int): KEntry? = section.getEntry(index).orNull?.let { KEntry(it) }

    fun getEntry(key: String): KEntry? = section.getEntry(key).orNull?.let { KEntry(it) }

    fun getSection(index: Int): KSection? = section.getSection(index).orNull?.let { KSection(it) }

    fun getSection(key: String): KSection? = section.getSection(key).orNull?.let { KSection(it) }

}