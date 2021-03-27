package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.KConfiguration
import net.dzikoysk.cdn.model.KNamedElement
import kotlin.reflect.KClass

class KCdn(private val cdn: Cdn) {

    fun parse(source: String): KConfiguration = KConfiguration(cdn.load(source))

    fun <T : Any> parse(scheme: KClass<T>, source: String): T = cdn.load(source, scheme.java)

    inline fun <reified T : Any> parseAs(source: String): T = this.parse(T::class, source)

    fun render(element: KNamedElement<*>): String = cdn.render(element.namedElement)

    fun render(entity: Any): String = cdn.render(entity)

    companion object {

        fun configure(): CdnSettings = CdnSettings()

    }

}