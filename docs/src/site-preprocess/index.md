---
layout: index
---

[![Build Status](https://travis-ci.org/nrinaudo/kantan.csv.svg?branch=master)](https://travis-ci.org/nrinaudo/kantan.csv)
[![codecov](https://codecov.io/gh/nrinaudo/kantan.csv/branch/master/graph/badge.svg)](https://codecov.io/gh/nrinaudo/kantan.csv)
[![Latest version](https://index.scala-lang.org/nrinaudo/kantan.csv/kantan.csv/latest.svg)](https://index.scala-lang.org/nrinaudo/kantan.csv)
[![Join the chat at https://gitter.im/nrinaudo/kantan.csv](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.csv)

kantan.csv is a library for CSV parsing and serialisation written in the
[Scala programming language](http://www.scala-lang.org).

## Getting started

kantan.csv is currently available for Scala 2.11 and 2.12.

The current version is `@VERSION@`, which can be added to your project with one or more of the following line(s)
in your SBT build file:

```scala
// Core library, included automatically if any other module is imported.
libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "@VERSION@"

// Java 8 date and time instances.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8" % "@VERSION@"

// Provides scalaz type class instances for kantan.csv, and vice versa.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-scalaz" % "@VERSION@"

// Provides cats type class instances for kantan.csv, and vice versa.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "@VERSION@"

// Automatic type class instances derivation.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "@VERSION@"

// Provides instances for joda time types.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-joda-time" % "@VERSION@"

// Provides instances for refined types.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-refined" % "@VERSION@"
```

Additionally, while kantan.csv comes with a default parser / serializer (that has
[pretty good]({{ site.baseurl }}/tut/benchmarks.html) performances), some people might prefer to use older, more
reputable implementations. The following engines are currently supported:

```scala
// opencsv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-opencsv" % "@VERSION@"

// commons-csv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-commons" % "@VERSION@"

// jackson-csv engine.
libraryDependencies += "com.nrinaudo" %% "kantan.csv-jackson" % "@VERSION@"
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
