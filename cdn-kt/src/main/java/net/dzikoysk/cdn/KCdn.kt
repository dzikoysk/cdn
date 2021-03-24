package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.KConfiguration
import net.dzikoysk.cdn.model.KConfigurationElement
import kotlin.reflect.KClass

class KCdn(private val cdn: CDN) {

    fun parse(source: String): KConfiguration = KConfiguration(cdn.parse(source))

    fun <T : Any> parse(scheme: KClass<T>, source: String): T = cdn.parse(scheme, source)

    fun render(element: KConfigurationElement<*>): String = cdn.render(element.configurationElement)

    fun render(entity: Any): String = cdn.render(entity)

    companion object {

        fun configure(): CdnSettings = CdnSettings()

        fun defaultInstance(): CDN = configure().build()

        fun defaultYamlLikeInstance(): CDN = configure()
                .enableYamlLikeFormatting()
                .build()
    }

}