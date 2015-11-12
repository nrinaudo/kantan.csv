---
layout: default
title:  "Generic type class derivation"
section: tutorial
---
Tabulate has a [shapeless](https://github.com/milessabin/shapeless) `generic` module used to derive type class instances
automatically. In order to use this feature, you must first modify your sbt file to include the following:

```scala
libraryDependencies += "com.nrinaudo" %% "tabulate-generic" % "0.1.6"
```

Once that's done, the following import is necessary:

```tut:silent
import tabulate.generic.codecs._
```

We'll also be using the normal imports across the course of this tutorial:

```tut:silent
import tabulate._
import tabulate.ops._
```

## Derived cell codecs
Tabulate is now capable of deriving cell encoders and decoders for sum types, such as sealed families of case classes.

A good example of that is cats' `Xor` type. This has dedicated support in the `tabulate-cats` library, but we can
now use the power of automatic derivation:

```tut
import cats.data._, cats.data.Xor._

val xor1: Xor[Int, Boolean] = Xor.right(true)
xor1.asCsvCell

val xor2: Xor[Int, Boolean] = Xor.left(10)
xor2.asCsvCell
```



## Derived row codecs

```tut
```