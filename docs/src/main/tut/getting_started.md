---
layout: default
title:  "Getting Started"
section: tutorial
---

Tabulate is currently available both for Scala 2.10 and 2.11.

The current version is `0.1.4-SNAPSHOT`, which can be added to your project with the following line in your SBT build
file:

```scala
libraryDependencies += "com.nrinaudo" %% "tabulate" % "0.1.4-SNAPSHOT"
```

A [scalaz-stream](https://github.com/scalaz/scalaz-stream) connector is also available through:

```scala
libraryDependencies += "com.nrinaudo" %% "tabulate-scalaz-stream" % "0.1.4-SNAPSHOT"
```


## Standard imports and implicits
All tutorials are going to assume the following imports are present:

```tut:silent
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
```

`com.nrinaudo.csv._` imports all the core classes, while `com.nrinaudo.csv.ops._` bring the various operators in scope.
 
Additionally, most methods used to open CSV data for reading or writing expect an implicit `scala.io.Codec` to be in
scope. I'll be using `ISO-LATIN-1` here, but bear in mind that there is no single charset that will work for all CSV
data. Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```tut:silent
implicit val codec = scala.io.Codec.ISO8859
```
