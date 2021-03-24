package net.dzikoysk.cdn

import kotlin.reflect.KClass

fun <T : Any> CDN.parse(type: KClass<T>, source: String): T = this.parse<T>(type.java, source)