# CDN ![CDN CI](https://github.com/dzikoysk/cdn/workflows/CDN%20CI/badge.svg)  [![Build Status](https://travis-ci.com/dzikoysk/cdn.svg?branch=master)](https://travis-ci.com/dzikoysk/cdn) [![Coverage Status](https://coveralls.io/repos/github/dzikoysk/cdn/badge.svg?branch=master)](https://coveralls.io/github/dzikoysk/cdn?branch=master) [![CodeFactor](https://www.codefactor.io/repository/github/dzikoysk/cdn/badge)](https://www.codefactor.io/repository/github/dzikoysk/cdn)
CDN *(Configuration Data Notation)* - fast, simple and enhanced standard of JSON5 *(JSON for Humans)* format for Java projects.


#### Features
- [x] Simple and easy to use
- [x] Automatic structure updates
- [x] Supports Java, Kotlin and Groovy
- [x] Performant and lightweight _(~ 90kB)_
- [x] Respecting properties order and comment entries
- [x] Bidirectional parse and compose of CDN sources
- [x] Serialization and deserialization of Java entities 
- [x] Indentation based configuration _(YAML-like)_
- [x] [`@Contract` support](https://www.jetbrains.com/help/idea/contract-annotations.html)
- [x] Null-safe querying API 
- [x] 95%+ test coverage
- [ ] Docs

#### Usage

<table>
<tr>
<th>Standard <i>(default)</i></th>
<td>Compatibility</td>
<th>Indentation based</th>
</tr>
<tr>
<td>
<pre lang="javascript">
# entry comment
key: value <br>
# section description
section {
  sub {
    // sub entry description
    subEntry: subValue
    list1 [
      1st element
      2nd element
    ]
  }
}
</pre>
</td>
<td align="center">
  ⇄ <br> <i>both are valid</i>
</td>
<td>
<pre lang="yaml">
# entry comment
key: value <br>
# section description
section:
  sub:
    // sub entry description
    subEntry: subValue
    list1:
      - 1st element
      - 2nd element
</pre>
</td>
</tr>
</table>

##### Class based

```java
public final class Configuration implements Serializable {

    @Description("# ~~~~~~~~~~~~~~~~~~~~~ #")
    @Description("#      Application      #")
    @Description("# ~~~~~~~~~~~~~~~~~~~~~ #")

    @Description("")
    @Description("# Hostname")
    public String hostname = "0.0.0.0";

}
```

Handling:

```java
// Load configuration
Configuration configuration = CDN.defaultInstance().parse(Configuration.class, configurationSource)
println configuration.hostname

// Save
configuration.hostname = "localhost"
FileUtils.overrideFile(configurationFile, CDN.defaultInstance().compose(configuration))

```

##### Manual

To load CDN source, use:

```java
// Parse configuration
Configuration configuration = CDN.getDefaultInstance().parse(source);
String keyValue = configuration.getString("key");
String subValue = configuration.getString("section.sub.subEntry");
Integer random = configuration.getInt("section.sectionEntry");

// Configuration to string 
String source = CDN.getDefaultInstance().render(configuration);
```

The output looks exactly like the input above. 

#### Maven

```xml
<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>cdn</artifactId>
    <version>1.6.0</version>
</dependency>
```

Repository:

```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/releases</url>
</repository>
```

#### Who's using

* [Reposilite](https://github.com/dzikoysk/reposilite)
* [Panda](https://github.com/panda-lang/panda)
* [FunnyGuilds](https://github.com/FunnyGuilds/FunnyGuilds)
