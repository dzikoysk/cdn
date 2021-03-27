package net.dzikoysk.cdn.model

open class KEntry(val entry: Entry) : KNamedElement<Unit>(entry) {

    val record: String = entry.record

}