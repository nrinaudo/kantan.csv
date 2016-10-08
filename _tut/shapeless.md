---
layout: tutorial
title: "Generic module"
section: tutorial
sort_order: 21
---
While kantan.csv goes out of its way to provide [default instances](default_instances.html) for as many types as it can,
some are made problematic by my strict rule against runtime reflection. Fortunately, [shapeless](http://shapeless.io)
provides _compile time_ reflection, which makes it possible for the `generic` module to automatically derive instances
for more common types and patterns.

The `generic` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.1.15"
```

If you're using Scala 2.10.x, you should also add the macro paradise plugin to your build:

```scala
libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

Let's first declare the imports we'll need in the rest of this tutorial:

```scala
import kantan.csv.ops._     // Provides CSV specific syntax.
import kantan.csv.generic._ // Provides automatic instance derivation.
```

The rest of this post will be a simple list of supported types.

## `CellEncoder`s and `CellDecoder`s

### Case classes of arity 1

All case classes of arity 1 have [`CellDecoder`] and [`CellEncoder`] instances, provided the type of their single field
also does.

Let's declare a (fairly useless) case class (we'll be making a more useful one in the next section):

```scala
case class Wrapper[A](a: A)
```

We can directly encode from and decode to instances of `Wrapper`:

```scala
scala> val decoded = "1, 2, 3\n4, 5, 6".unsafeReadCsv[List, List[Wrapper[Int]]](',', false)
decoded: List[List[Wrapper[Int]]] = List(List(Wrapper(1), Wrapper(2), Wrapper(3)), List(Wrapper(4), Wrapper(5), Wrapper(6)))

scala> decoded.asCsv(',')
res0: String =
"1,2,3
4,5,6
"
```

### Sum types

We can also get free [`CellDecoder`] and [`CellEncoder`] instances for sum types where all alternatives have a
[`CellDecoder`] and [`CellEncoder`]. For example:

```scala
sealed abstract class Or[+A, +B]
case class Left[A](value: A) extends Or[A, Nothing]
case class Right[B](value: B) extends Or[Nothing, B]
```

`Left` is a unary case class and will have a [`CellDecoder`] if its type parameter has one, and the same goes for
`Right`. This allows us to write:

```scala
scala> val decoded = "1,true\nfalse,2".unsafeReadCsv[List, List[Int Or Boolean]](',', false)
decoded: List[List[Or[Int,Boolean]]] = List(List(Left(1), Right(true)), List(Right(false), Left(2)))

scala> decoded.asCsv(',')
res1: String =
"1,true
false,2
"
```

## Rows

### Case classes

All case classes have [`RowEncoder`] and [`RowDecoder`] instances, provided all their fields also do.

Take, for example, a custom [`Tuple2`] implementation (using an actual [`Tuple2`] might not be very convincing, as
it's supported by kantan.csv without needing the `generic` module):

```scala
case class CustomTuple2[A, B](a: A, b: B)
```

We can encode from and decode to that type for free:

```scala
scala> val decoded = "1,\n2,false".unsafeReadCsv[List, CustomTuple2[Int, Option[Boolean]]](',', false)
decoded: List[CustomTuple2[Int,Option[Boolean]]] = List(CustomTuple2(1,None), CustomTuple2(2,Some(false)))

scala> decoded.asCsv(',')
res2: String =
"1,
2,false
"
```

It is *very* important to realise that while this is a pretty nice feature, it's also a very limited one. The only
time where you can get your case class codecs derived automatically is when the case class' fields and the CSV columns
are in exactly the same order. Any other scenario and you need to use old fashioned
[encoders](arbitrary_types_as_rows.html) and [decoders](rows_as_arbitrary_types.html).

### Sum types

As with cells, sum types have [`RowEncoder`] and [`RowDecoder`] instances provided their all their alternatives also do.

In the following example:

* `(Int, Boolean)` has both, since it's a [`Tuple2`] of primitive types.
* `CustomTuple2[String, Option[Boolean]]` has both, since it's a case class where all fields also do.

```scala
scala> "1,true\nfoobar,".unsafeReadCsv[List, (Int, Boolean) Or CustomTuple2[String, Option[Boolean]]](',', false)
res3: List[Or[(Int, Boolean),CustomTuple2[String,Option[Boolean]]]] = List(Left((1,true)), Right(CustomTuple2(foobar,None)))
```

[`RowDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`CellCodec`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellCodec[A]=kantan.codecs.Codec[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`Tuple2`]:http://www.scala-lang.org/api/current/index.html#scala.Tuple2
