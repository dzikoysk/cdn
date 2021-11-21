# CDN ![CDN CI](https://github.com/dzikoysk/cdn/workflows/CDN%20CI/badge.svg) [![codecov](https://codecov.io/gh/dzikoysk/cdn/branch/master/graph/badge.svg?token=374BLLP5OI)](https://codecov.io/gh/dzikoysk/cdn) [![CodeFactor](https://www.codefactor.io/repository/github/dzikoysk/cdn/badge)](https://www.codefactor.io/repository/github/dzikoysk/cdn)
Simple and fast configuration library for JVM based apps, powered by CDN *(Configuration Data Notation)* format, based on enhanced JSON for Humans standard. Handles [CDN](https://github.com/dzikoysk/cdn), [JSON](https://www.json.org) and [YAML](https://yaml.org)-like configurations with built-in support for comments and automatic scheme updates.

## Overview
- [x] Supports Java, Kotlin _(dedicated extension)_ and Groovy
- [x] Automatically updates configuration structure and migrates user's values
- [x] Lightweight ~ 50kB (no extra dependencies) 
- [x] Respects properties order and comment entries
- [x] Bidirectional parse and render of CDN sources
- [x] Serialization and deserialization of Java entities 
- [x] Indentation based configuration _(YAML-like)_
- [x] Compatibility with JSON format
- [x] Null-safe querying API with [`@Contract`](https://www.jetbrains.com/help/idea/contract-annotations.html) support
- [x] 95%+ test coverage
- [x] Actively supported and docs

### Table of Contents
* [Installation](#installation)
    * [Maven artifact](#gradle)
    * [Manual](#manual)
* [Using the Library](#using-the-library)
    * [TL;DR](#tldr)
    * [Load configuration](#load-configuration) 
    * [Update properties](#update-properties) 
    * [Save configuration](#save-configuration) 
    * [Supported formats](#supported-formats)

### Installation

#### Gradle

```groovy
repositories {
    maven { url 'https://repo.panda-lang.org/releases' }
}

dependencies {
    // Default
    implementation 'net.dzikoysk:cdn:1.12.3'
    // Kotlin wrapper
    implementation 'net.dzikoysk:cdn-kt:1.12.3'
}
```

#### Manual

You can find all available versions in the repository:

* [Repository - Artifact net.dzikoysk:cdn](https://repo.panda-lang.org/#/releases/net/dzikoysk/cdn)

### Using the library

#### TLDR
A brief summary of how to use the library.

```java
public final class AwesomeConfig {

    @Description("# Comment")
    public String property = "default value";

}
```

Handling:

```java
Cdn cdn = CdnFactory.createStandard();
File configurationFile = new File("./config.cdn");

// Load configuration
AwesomeConfig configuration = cdn.load(configurationFile, AwesomeConfig.class)
// Modify configuration
configuration.property = "modified value";
// Save configuration
cdn.render(configuration, configurationFile);
```

To explore all features, take a look at other chapters.

#### Load configuration

By default, CDN is meant to use class-based configuration.
It means that configurations are accessed through the standard Java instance.
Let's say we'd like to maintain this configuration:

```hocon
hostname: 'localhost'
```

At first, every configuration is loaded into the `Configuration` object.

```java
Configuration configuration = cdn.load("hostname: localhost")
String hostname = configuration.getString("hostname", "default value, if the requested one was not found")
```

To avoid such a cringe configuration handling, we can just map this configuration into the Java object.
Let's declare the scheme:

```java
public final class Config {
    
    public String hostname = "default value";
    
}
```

The last thing to do is to provide this class during the load process:

```java
Config config = cdn.load("hostname: localhost", Config.class)
config.hostname // returns 'localhost'
```

#### Update properties

Configurations can be updated in both variants. 
As you can see in the previous chapter, we've declared hostname field as non-final.
It means we can just update it's value and CDN will update this field during next render.

```java
config.hostname = "new value";
```

For configuration without scheme, we can use `setString` method:

```java
configuration.setString("hostname", "new value");
```

#### Save configuration

CDN can render configuration elements and entities using `render` methods.
The output depends on the installed format. 

#### Supported formats
Various formats ae supported through the `Feature` api. 
Available format features:

* DefaultFeature (CDN)
* JsonFeature
* YamlFeature

Output comparison:
 
<table>
 <tr>
  <th>Standard</th>
  <th>JSON feature</th>
  <th>YAML-like feature</th>
 </tr>
 <tr>
  <td>
   <pre lang="javascript">
# entry comment
key: value
# section description
section {
  list [
    1st element
    2nd element
  ]
}
   </pre>
  </td>
  <td>
   <pre lang="javascript">
# entry comment
"key": "value",
# section description
"section": {
 "list": [
  "1st element",
  "2nd element"
 ]
}
   </pre>
  </td>
  <td>
   <pre lang="yaml">
# entry comment
key: value <br>
# section description
section:
  list:
    - 1st element
    - 2nd element
   </pre>
  </td>
 </tr>
</table>

#### Who's using
* [Panda](https://github.com/panda-lang/panda)
* [Reposilite](https://github.com/dzikoysk/reposilite)
* [FunnyGuilds](https://github.com/FunnyGuilds/FunnyGuilds)
