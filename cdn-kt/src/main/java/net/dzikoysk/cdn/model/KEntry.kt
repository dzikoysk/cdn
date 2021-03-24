package net.dzikoysk.cdn.model

open class KEntry(val entry: Entry) : KConfigurationElement<String>(entry) {

    val record: String = entry.record

}