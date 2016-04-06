---
layout: tutorial
title: "Generic module"
section: tutorial
sort: 21
---
While kantan.csv goes out of its way to provide [default instance](default_instances.html) for as many types as it can,
some are made problematic by my strict rule against runtime reflection. Fortunately, [shapeless](http://shapeless.io)
provides _compile time_ reflection, which makes it possible for the `generic` module to automatically derive instances
for more common types and patterns.

The `generic` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.1.9"
```

Let's first declare the imports we'll need in the rest of this tutorial:

```tut:silent
import kantan.csv.ops._     // Provides CSV specific syntax.
import kantan.csv.generic._ // Provides automatic instance derivation.
```

The rest of this post will be a simple list of supported types.

## `CellEncoder`s and `CellDecoder`s

### Case objects

While not the most obviously useful instance, case objects automatically have a [`CellDecoder`] and [`CellEncoder`]
instance, working from and to the empty string.

Here's a case object example:

```tut:silent
case object Foo
```

This can be encoded and decoded without any specific declaration: 

```tut
val decoded = ",,\n,,".unsafeReadCsv[List, List[Foo.type]](',', false)

decoded.asCsv(',')
```

While this might not seem terribly useful, its purpose will become clearer when dealing with sum types. 


### Case classes of arity 1

All case classes of arity 1 have [`CellDecoder`] and [`CellEncoder`] instances, provided the type of their single field
also does.

Let's declare a (fairly useless) case class (we'll be making a more useful one in the next section):

```tut:silent
case class Wrapper[A](a: A)
```

We can directly encode from and decode to instances of `Wrapper`:

```tut
val decoded = "1, 2, 3\n4, 5, 6".unsafeReadCsv[List, List[Wrapper[Int]]](',', false)

decoded.asCsv(',')
```

### Sum types

We can also get free [`CellDecoder`] and [`CellEncoder`] instances for sum types where all alternatives have a
[`CellDecoder`] and [`CellEncoder`]. For example:

```tut:silent
sealed abstract class Maybe[+A]
case class Just[A](value: A) extends Maybe[A]
case object Nothing extends Maybe[Nothing]
```

`Just` is a unary case class, so it has a [`CellCodec`] instance if its type argument has one. `Nothing` is a case
object, so it automatically has [`CellCodec`]. This allows us to write:

```tut
val decoded = "1,, 3\n4, , 6".unsafeReadCsv[List, List[Maybe[Wrapper[Int]]]](',', false)

decoded.asCsv(',')
```

Here's another common example, to show that sum types where both alternatives hold values are supported:

```tut:silent
sealed abstract class Xor[+A, +B]
case class Left[A](value: A) extends Xor[A, Nothing]
case class Right[B](value: B) extends Xor[Nothing, B]
```

Encoding and decoding work just as well as before:

```tut
val decoded = "1,true\nfalse,2".unsafeReadCsv[List, List[Xor[Int, Boolean]]](',', false)

decoded.asCsv(',')
```

## Rows

### Case classes

All case classes have [`RowEncoder`] and [`RowDecoder`] instances, provided all their fields also do.
 
Take, for example, a custom [`Tuple2`] implementation (using an actual [`Tuple2`] might not be very convincing, as
it's supported by kantan.csv without needing the `generic` module):

```tut:silent
case class CustomTuple2[A, B](a: A, b: B)
```

We can encode from and decode to that type for free:

```tut
val decoded = "1,\n2,false".unsafeReadCsv[List, CustomTuple2[Int, Maybe[Boolean]]](',', false)

decoded.asCsv(',')
```

It is *very* important to realise that while this is a pretty nice feature, it's also a very limited one. The only
time where you can get your case class codecs derived automatically is when the case class' fields and the CSV columns
are in exactly the same order. Any other scenario and you need to use old fashioned
[encoders](arbitrary_types_as_rows.html) and [decoders](rows_as_arbitrary_types.html).

### Sum types

As with cells, sum types have [`RowEncoder`] and [`RowDecoder`] instances provided their all their alternatives also do.

In the following example:

* `(Int, Boolean)` has both, since it's a [`Tuple2`] of primitive types.
* `CustomTuple2[String, Maybe[Boolean]]` has both, since it's a case class where all fields also do.

```tut
val decoded = "1,true\nfoobar,".unsafeReadCsv[List, Xor[(Int, Boolean), CustomTuple2[String, Maybe[Boolean]]]](',', false)

decoded.asCsv(',')
```

[`RowDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`CellCodec`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellCodec[A]=kantan.codecs.Codec[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`Tuple2`]:http://www.scala-lang.org/api/current/index.html#scala.Tuple2