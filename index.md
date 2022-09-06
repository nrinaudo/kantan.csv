---
layout: index
---

[![Build Status](https://travis-ci.org/nrinaudo/kantan.csv.svg?branch=master)](https://travis-ci.org/nrinaudo/kantan.csv)
[![codecov](https://codecov.io/gh/nrinaudo/kantan.csv/branch/master/graph/badge.svg)](https://codecov.io/gh/nrinaudo/kantan.csv)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.csv_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.csv_2.13)
[![Join the chat at https://gitter.im/nrinaudo/kantan.csv](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.csv)

kantan.csv is a library for CSV parsing and serialization written in the
[Scala programming language](http://www.scala-lang.org).

## Getting started

kantan.csv is currently available for Scala 2.12 and 2.13.

The current version is `0.7.0`, which can be added to your project with one or more of the following line(s)
in your SBT build file:

```scala
// Core library, included automatically if any other module is imported.
libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "0.7.0"

// Java 8 date and time instances.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8" % "0.7.0"

// Provides scalaz type class instances for kantan.csv, and vice versa.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-scalaz" % "0.7.0"

// Provides cats type class instances for kantan.csv, and vice versa.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "0.7.0"

// Automatic type class instances derivation.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0"

// Provides instances for refined types.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-refined" % "0.7.0"

// Provides instances for enumeratum types.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-enumeratum" % "0.7.0"

// Provides instances for libra types.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-libra" % "0.7.0"
```

Additionally, while kantan.csv comes with a default parser / serializer (that has
[pretty good]({{ site.baseurl }}/benchmarks.html) performances), some people might prefer to use older, more
reputable implementations. The following engines are currently supported:

```scala
// commons-csv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-commons" % "0.7.0"

// jackson-csv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-jackson" % "0.7.0"
```


## Motivation

CSV is an unreasonably popular data exchange format. It suffers from poor (or at the very least late) standardisation,
and is often a nightmare to work with when it contains more complex data than just lists of numerical values.

I started writing kantan.csv when I realised I was spending more time dealing with the data _container_ than the
data itself. My goal is to abstract CSV away as much as possible and allow developers to describe their data and where
it comes from, and then just work with it.

kantan.csv is meant to be [RFC](https://tools.ietf.org/html/rfc4180) compliant, but flexible enough that it should
parse any sane variation on the format. Should you find CSV files that don't parse, please file an issue and I'll look
into it.

While I'm pretty happy with kantan.csv, or at least the direction it's headed, I do not pretend that it will fit
all use cases. It fits mine, but might not work for everyone. I'm happy to hear suggestions on how this can be
addressed, though.
