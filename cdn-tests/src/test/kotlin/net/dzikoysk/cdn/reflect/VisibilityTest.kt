package net.dzikoysk.cdn.reflect

import org.junit.jupiter.api.Test

class VisibilityTest {

    @Test
    fun `should pass all modifiers`() {
        ModifierTester(TestClasses.Public::class.java)
            .with(Visibility.PUBLIC)
            .with(Visibility.PACKAGE_PRIVATE)
            .with(Visibility.PROTECTED)
            .with(Visibility.PRIVATE)
            .test()
    }

    @Test
    fun `should pass only package-private, protected and private modifiers`() {
        ModifierTester(TestClasses.PackagePrivate::class.java)
            .with(Visibility.PACKAGE_PRIVATE)
            .with(Visibility.PROTECTED)
            .with(Visibility.PRIVATE)
            .test()
    }

    @Test
    fun `should pass only protected and private modifiers`() {
        ModifierTester(TestClasses.Protected::class.java)
            .with(Visibility.PROTECTED)
            .with(Visibility.PRIVATE)
            .test()
    }

    class ModifierTester(private val mod: Int) {

        constructor(type: Class<*>) : this(type.modifiers)

        private val included: MutableList<Visibility> = ArrayList()
        private val excluded: MutableList<Visibility> = mutableListOf(
            Visibility.PUBLIC, Visibility.PRIVATE, Visibility.PROTECTED, Visibility.PACKAGE_PRIVATE)

        fun with(visibility: Visibility): ModifierTester {
            excluded.remove(visibility)
            included.add(visibility)
            return this
        }

        fun test(): ModifierTester {
            excluded.forEach { assert(it.isNotVisible(mod)) { "has ${it.name}" } }
            included.forEach { assert(it.isVisible(mod)) { "has not ${it.name}" } }
            return this
        }

    }

}

