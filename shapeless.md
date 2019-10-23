---
layout: scala mdocorial
title: "Generic module"
section: scala mdocorial
sort_order: 25
---

While kantan.csv goes out of its way to provide [default instances](default_instances.html) for as many types as it can,
some are made problematic by my strict rule against runtime reflection. Fortunately, [shapeless](http://shapeless.io)
provides _compile time_ reflection, which makes it possible for the `generic` module to automatically derive instances
for more common types and patterns.

The `generic` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1-SNAPSHOT"
```

Let's first declare the imports we'll need in the rest of this scala mdocorial:

```scala
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
```

The rest of this post will be a simple list of supported types.

## `CellEncoder`s and `CellDecoder`s

### Case classes of arity 1

All case classes of arity 1 have [`CellDecoder`] and [`CellEncoder`] instances, provided the type of their single field
also does.

Let's declare a (fairly useless) case class (we'll be making a more useful one in the next section):

```scala
final case class Wrapper[A](a: A)
```

We can directly encode from and decode to instances of `Wrapper`:

```scala
val decoded1 = "1, 2, 3\n4, 5, 6".unsafeReadCsv[List, List[Wrapper[Int]]](rfc)
// decoded1: List[List[Wrapper[Int]]] = List(
//   List(Wrapper(1), Wrapper(2), Wrapper(3)),
//   List(Wrapper(4), Wrapper(5), Wrapper(6))
// )

decoded1.asCsv(rfc)
// res0: String = """1,2,3
// 4,5,6
// """
```

### Sum types

We can also get free [`CellDecoder`] and [`CellEncoder`] instances for sum types where all alternatives have a
[`CellDecoder`] and [`CellEncoder`]. For example:

```scala
sealed abstract class Or[+A, +B]
final case class Left[A](value: A) extends Or[A, Nothing]
final case class Right[B](value: B) extends Or[Nothing, B]
```

`Left` is a unary case class and will have a [`CellDecoder`] if its type parameter has one, and the same goes for
`Right`. This allows us to write:

```scala
val decoded2 = "1,true\nfalse,2".unsafeReadCsv[List, List[Int Or Boolean]](rfc)
// decoded2: List[List[Or[Int, Boolean]]] = List(
//   List(Left(1), Right(true)),
//   List(Right(false), Left(2))
// )

decoded2.asCsv(rfc)
// res1: String = """1,true
// false,2
// """
```

## Rows

### Case classes

All case classes have [`RowEncoder`] and [`RowDecoder`] instances, provided all their fields also do.

Take, for example, a custom [`Tuple2`] implementation (using an actual [`Tuple2`] might not be very convincing, as
it's supported by kantan.csv without needing the `generic` module):

```scala
final case class CustomTuple2[A, B](a: A, b: B)
```

We can encode from and decode to that type for free:

```scala
val decoded3 = "1,\n2,false".unsafeReadCsv[List, CustomTuple2[Int, Option[Boolean]]](rfc)
// decoded3: List[CustomTuple2[Int, Option[Boolean]]] = List(
//   CustomTuple2(1, None),
//   CustomTuple2(2, Some(false))
// )

decoded3.asCsv(rfc)
// res2: String = """1,
// 2,false
// """
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
"1,true\nfoobar,".unsafeReadCsv[List, (Int, Boolean) Or CustomTuple2[String, Option[Boolean]]](rfc)
// res3: List[Or[(Int, Boolean), CustomTuple2[String, Option[Boolean]]]] = List(
//   Left((1, true)),
//   Right(CustomTuple2("foobar", None))
// )
```

[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/RowDecoder$.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`CellCodec`]:{{ site.baseurl }}/api/kantan/csv/package$$CellCodec.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`Tuple2`]:http://www.scala-lang.org/api/current/scala/Tuple2.html
