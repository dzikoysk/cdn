package net.dzikoysk.cdn

import net.dzikoysk.cdn.annotation.KotlinMemberResolver
import net.dzikoysk.cdn.source.Source

inline fun <reified T : Any> Cdn.loadAs(source: Source): T =
    this.load(source, T::class.java)

fun CdnSettings.registerKotlinModule() : CdnSettings =
    this.withAnnotationResolver(KotlinMemberResolver())