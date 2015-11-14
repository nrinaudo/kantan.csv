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
import tabulate.ops._
```

`tabulate.ops._` brings in the various operators we'll need in this tutorial, while `tabulate.generic.codecs` contains
all the automatic type class instance derivations.


## Derived cell codecs
With this setup, we've now allowed Tabulate to derive cell codecs for some algebraic data types (referred to as ADTs
in the rest of this document). Note the *some* in the previous sentence: we can't create cell codecs for product types,
for instance, as that would mean storing more than one value per CSV cell.

As a first step, let's create a type that we need to derive instances for: `Maybe`, a simplified implementation of the
standard library's [Option](http://www.scala-lang.org/api/current/index.html#scala.Option).

```tut:silent
sealed trait Maybe[+A]

object Maybe {
  case class Just[A](a: A) extends Maybe[A]
  case object Empty extends Maybe[Nothing]

  def just[A](a: A): Maybe[A] = Just(a)
  def empty[A]: Maybe[A] = Empty
}
```

While we *could* write a `CellEncoder` and `CellDecoder` instance for this type by hand, there is no need: the `generic`
module is capable of automatically deriving such instances for us.

```tut
Maybe.just(1).asCsvCell
Maybe.empty[Int].asCsvCell

"1".parseCsvCell[Maybe[Int]]
"".parseCsvCell[Maybe[Int]]
```

It's important to stress that these instances are generated at *compile* time and that absolutely no runtime reflection
is involved. No nasty type error at runtime, if the code compiles, it will execute nicely.


## Derived row codecs

```tut
Maybe.just(1).asCsvRow
Maybe.empty[Int].asCsvRow

Seq("1", "1").parseCsvRow[Maybe[(Int, Int)]]
Seq("").parseCsvRow[Maybe[(Int, Int)]]
```