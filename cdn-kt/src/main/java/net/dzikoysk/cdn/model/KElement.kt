package net.dzikoysk.cdn.model

open class KElement<T : Any>(val element: Element<T>) {

    val value: T = element.value

    val description: List<String?> = element.description

}