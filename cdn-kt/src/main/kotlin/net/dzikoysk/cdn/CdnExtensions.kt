package net.dzikoysk.cdn

import net.dzikoysk.cdn.reflect.KotlinMemberResolver
import net.dzikoysk.cdn.source.Source
import panda.std.Result

inline fun <reified T : Any> Cdn.loadAs(source: Source): Result<T, CdnException> =
    this.load(source, T::class.java)

fun CdnSettings.registerKotlinModule() : CdnSettings =
    this.withMemberResolver(KotlinMemberResolver())