package net.dzikoysk.cdn

import net.dzikoysk.cdn.annotation.KotlinAnnotationResolver

fun CdnSettings.registerKotlinModule() : CdnSettings {
    this.changeAnnotationResolver(KotlinAnnotationResolver())
    return this
}