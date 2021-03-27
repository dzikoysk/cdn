package net.dzikoysk.cdn.model

open class KNamedElement<T : Any>(val namedElement: NamedElement<T>) {

    val value: T = namedElement.value

    val description: List<String?> = namedElement.description

    val name: String = namedElement.name

}