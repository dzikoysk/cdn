package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.KConfiguration
import net.dzikoysk.cdn.model.KConfigurationElement
import kotlin.reflect.KClass

class KCdn(private val cdn: Cdn) {

    fun parse(source: String): KConfiguration = KConfiguration(cdn.parse(source))

    fun <T : Any> parse(scheme: KClass<T>, source: String): T = cdn.parse(scheme.java, source)

    inline fun <reified T : Any> parseAs(source: String): T = this.parse(T::class, source)

    fun render(element: KConfigurationElement<*>): String = cdn.render(element.configurationElement)

    fun render(entity: Any): String = cdn.render(entity)

    companion object {

        fun configure(): CdnSettings = CdnSettings()

        fun defaultInstance(): Cdn = configure().build()

        fun defaultYamlLikeInstance(): Cdn = configure()
                .enableYamlLikeFormatting()
                .build()
    }

}