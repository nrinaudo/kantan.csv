---
layout: default
title:  "Generic type class derivation"
section: tutorial
---
Tabulate has a [shapeless](https://github.com/milessabin/shapeless) `generic` module used to derive type class instances
automatically. In order to use this feature, you must first modify your sbt file to include the following:

```scala
libraryDependencies += "com.nrinaudo" %% "tabulate-generic" % "0.1.7"
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
in the rest of this document). Note the *some* in the previous sentence: we can't create cell codecs for product types
of arity greater than 1, for instance, as that would mean storing more than one value per CSV cell.

### Simple example: `Maybe`

As an example, let's create a type that we need to derive instances for: `Maybe`, a simplified implementation of the
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

"1".decodeCsvCell[Maybe[Int]]
"".decodeCsvCell[Maybe[Int]]
```

It's important to stress that these instances are generated at *compile* time and that absolutely no runtime reflection
is involved. No nasty type error at runtime, if the code compiles, it will execute nicely.


### Case classes (product types)
In the previous example, we saw tabulate derive cell codecs for:

* case objects (or case classes of arity 0): `Empty`.
* case classes of arity 1: `Just`.

This turns out to be the complete list of case classes for which Tabulate knows to derive cell codecs. Any other
arity would require some sort of non-obvious transformation of the data - say that you have an `(Int, Int)`, how do
you turn that into a single value, preferably in a reversible fashion? There are various solutions, but none that we
can decide on automatically, and you'll need to make the choice by providing a hand-written codec. 


### Sum types
While we've seen that Tabulate could derive cell codec instances for `Just` and `Empty` in our previous example, this
is not actually what we were working with: rather, we requested a codec for `Maybe`, which is made up of `Just` and
`Empty`.

This is a common encoding of sum types in Scala: the type itself (`Maybe`) is declared as a sealed trait, and its
alternatives are case classes that extend it (`Just` and `Empty`).

Tabulate can generate cell codec instances for all such types, provided all alternatives have cell codecs themselves.
 

## Derived row codecs
The `generic` module also allows Tabulate to derive instances of row codecs.
 
The same `Maybe` example works just as expected:

```tut
Maybe.just((1, 2)).asCsvRow
Maybe.empty[(Int, Int)].asCsvRow

Seq("1", "2").decodeCsvRow[Maybe[(Int, Int)]]
Seq.empty[String].decodeCsvRow[Maybe[(Int, Int)]]
```

### Case classes (product types)
Tabulate can derive row codecs for case classes of any arity:

* arity 0: turns into empty rows. Note that these can be surprising, as by specification they will be ignored when
  serialising. This is how `Empty[(Int, Int)]` is encoded.
* arity 1: uses the underlying type's row codec. This is how `Just[(Int, Int)]` is encoded.
* arity 2+: each field is serialised as a CSV cell in the order in which they are declared.

Note that while this sounds more convenient than the core module's `caseCodecXXX` methods, it's actually rather less
flexible: you have no control over the order in which case class fields are mapped to CSV cells, and might find yourself
having to write explicit instances if your input data isn't in *just* the right order.

### Sum types
Row codecs are derived for sum types in exactly the same way they are for cell codecs, provided each alternative has a
row codec itself. There is no known caveat here, except maybe the usual shapeless one that you'll notice a sharp
increase in compile times.