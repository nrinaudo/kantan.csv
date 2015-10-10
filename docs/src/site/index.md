---
layout: default
---

[![Build Status](https://travis-ci.org/nrinaudo/tabulate.svg?branch=master)](https://travis-ci.org/nrinaudo/tabulate)
[![codecov.io](http://codecov.io/github/nrinaudo/tabulate/coverage.svg?branch=master)](http://codecov.io/github/nrinaudo/tabulate?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/tabulate_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/tabulate_2.11)
[![Join the chat at https://gitter.im/nrinaudo/tabulate](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/tabulate)

Tabulate is a library for CSV parsing and serialisation written in the
[Scala programming language](http://www.scala-lang.org).

## Getting started

Tabulate is currently available both for Scala 2.10 and 2.11.

The current version is `0.1.4`, which can be added to your project with one or more of the following line(s)
in your SBT build file:

```scala
// Core library, included automatically if any other module is imported.
libraryDependencies += "com.nrinaudo" %% "tabulate" % "0.1.4"

// Provides cats type class instances for tabulate, and vice versa.
libraryDependencies += "com.nrinaudo" %% "tabulate-scalaz" % "0.1.4"

// Treat CSV data as sources and sinks.
libraryDependencies += "com.nrinaudo" %% "tabulate-scalaz-stream" % "0.1.4"

// Provides cats type class instances for tabulate, and vice versa.
libraryDependencies += "com.nrinaudo" %% "tabulate-cats" % "0.1.4"
```


## Motivation

CSV is an unreasonably popular data exchange format. It suffers from poor (or at the very least late) standardisation,
and is often a nightmare to work with when it contains more complex data than just lists of numerical values.

I started writing Tabulate when I realised I was spending more time dealing with the data _container_ than the
data itself. My goal is to abstract CSV away as much as possible and allow developers to describe their data and where
it comes from, and then just work with it.

Tabulate is meant to be [RFC](https://tools.ietf.org/html/rfc4180) compliant, but flexible enough that it should
parse any sane variation on the format. Should you find CSV files that don't parse, please file an issue and I'll look
into it.

While I'm pretty happy with Tabulate, or at least the direction it's headed, I do not pretend that it will fit
all use cases. It fits mine, but might not work for everyone. I'm happy to hear suggestions on how this can be
addressed, though.


## Tutorials

The following tutorials are available:
{% for x in site.tut %}
{% if x.status != "wip" %}
* [{{ x.title }}]({{ site.baseurl }}{{ x.url }})
{% endif %}
{% endfor %}
