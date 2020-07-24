# CDN ![CDN CI](https://github.com/dzikoysk/cdn/workflows/CDN%20CI/badge.svg)  [![Build Status](https://travis-ci.com/dzikoysk/cdn.svg?branch=master)](https://travis-ci.com/dzikoysk/cdn) [![Coverage Status](https://coveralls.io/repos/github/dzikoysk/cdn/badge.svg?branch=master)](https://coveralls.io/github/dzikoysk/cdn?branch=master) [![CodeFactor](https://www.codefactor.io/repository/github/dzikoysk/cdn/badge)](https://www.codefactor.io/repository/github/dzikoysk/cdn)
CDN *(Configuration Data Notation)* - fast, simple and enhanced standard of JSON5 *(JSON for Humans)* format for Java projects.


#### Features
- [x] Simple and easy to use
- [x] Performant
- [x] Respecting comment entries
- [x] Bidirectional parse and compose of CDN sources
- [x] Serialization and deserialization of Java entities 
- [x] 90%+ test coverage
- [x] Indentation based configuration
- [ ] Docs

#### Usage
Let's say we want to maintain the following configuration:
```haml
// entry
key: value
# section description
section {
  sub {
    // sub entry description
    subEntry: subValue
  }

  # section
  // entry
  # description
  sectionEntry: 7
}
```

To load CDN source, use:

```java
Configuration configuration = CDN.parse(source);

// get some values
String keyValue = configuration.getString("key");
String subValue = configuration.getString("section.sub.subEntry");
Integer random = configuration.getInt("section.sectionEntry");
```

You can also compose CDN element to text:

```java
String source = CDN.compose(configuration);
```

The output looks exactly like the input above. 

#### Maven

```xml
<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>cdn</artifactId>
    <version>1.0.0</version>
</dependency>
```

Repository:

```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/releases</url>
</repository>
```
