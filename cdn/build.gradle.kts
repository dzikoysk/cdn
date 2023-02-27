description = "CDN | CDN library"

dependencies {
    api("org.panda-lang:expressible:1.3.0")
    api("org.panda-lang:panda-utilities:0.5.3-alpha") {
        exclude(group = "org.javassist", module = "javassist")
    }
    api("org.jetbrains:annotations:24.0.0")
}