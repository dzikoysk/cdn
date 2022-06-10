package net.dzikoysk.cdn.reflect

import org.junit.jupiter.api.Test

class ModifierTest {

    @Test
    fun `should find only public modifier classes`() {
        ModifierTester(TestClasses.Public::class.java)
            .with(Modifier.PUBLIC)
            .test()
    }

    @Test
    fun `should find only public, static and abstract modifiers classes`() {
        ModifierTester(TestClasses.PublicStaticAbstract::class.java)
            .with(Modifier.PUBLIC)
            .with(Modifier.STATIC)
            .with(Modifier.ABSTRACT)
            .test()
    }

    @Test
    fun `should find only protected, static and abstract modifiers classes`() {
        ModifierTester(TestClasses.ProtectedStaticAbstract::class.java)
            .with(Modifier.PROTECTED)
            .with(Modifier.STATIC)
            .with(Modifier.ABSTRACT)
            .test()
    }

    @Test
    fun `should find only package-private modifier classes`() {
        ModifierTester(TestClasses.PackagePrivate::class.java)
            .with(Modifier.PACKAGE_PRIVATE)
            .test()
    }

    @Test
    fun `should find only package-private and static modifiers classes`() {
        ModifierTester(TestClasses.PackagePrivateStatic::class.java)
            .with(Modifier.PACKAGE_PRIVATE)
            .with(Modifier.STATIC)
            .test()
    }

    @Test
    fun `should find only package-private, static and abstract modifiers classes`() {
        ModifierTester(TestClasses.PackagePrivateStaticAbstract::class.java)
            .with(Modifier.PACKAGE_PRIVATE)
            .with(Modifier.STATIC)
            .with(Modifier.ABSTRACT)
            .test()
    }

    class ModifierTester(private val mod: Int) {

        constructor(type: Class<*>) : this(type.modifiers)

        private val included: MutableList<Modifier> = ArrayList()
        private val excluded: MutableList<Modifier> = mutableListOf(Modifier.PUBLIC, Modifier.PRIVATE, Modifier.PROTECTED, Modifier.PACKAGE_PRIVATE, Modifier.STATIC, Modifier.FINAL, Modifier.SYNCHRONIZED, Modifier.VOLATILE, Modifier.TRANSIENT, Modifier.NATIVE, Modifier.INTERFACE, Modifier.ABSTRACT, Modifier.STRICT)

        fun with(modifier: Modifier): ModifierTester {
            excluded.remove(modifier)
            included.add(modifier)
            return this
        }

        fun test(): ModifierTester {
            excluded.forEach { assert(it.hasNot(mod)) }
            included.forEach { assert(it.has(mod)) }
            return this
        }

    }

}

