package net.dzikoysk.cdn

import net.dzikoysk.cdn.annotation.KotlinMemberResolver

fun CdnSettings.registerKotlinModule() : CdnSettings {
    this.withAnnotationResolver(KotlinMemberResolver())
    return this
}