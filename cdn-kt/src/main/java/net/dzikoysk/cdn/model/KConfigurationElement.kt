package net.dzikoysk.cdn.model

open class KConfigurationElement<T : Any>(val configurationElement: ConfigurationElement<T>) {

    val value: T = configurationElement.value

    val description: List<String?> = configurationElement.description

    val name: String = configurationElement.name

}