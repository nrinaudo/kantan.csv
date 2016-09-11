---
layout: tutorial
title: "Cats module"
section: tutorial
sort_order: 21
---
Kantan.csv has a [cats](https://github.com/typelevel/cats) module that is, in its current incarnation, fairly bare
bones: it provides decoders for [`Xor`] as well as a few useful type class instances.

The `cats` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "0.1.14"
```

You then need to import the corresponding package:

```tut:silent
import kantan.csv.cats._
```

## `Xor` codecs

For any two types `A` and `B` that each have a [`CellEncoder`], there exists a
`CellEncoder[A Xor B]`. If `A` and `B` each have a [`RowEncoder`], there exists a `RowEncoder[A Xor B]`.

By the same token, for any two types `A` and `B` that each have a [`CellDecoder`], there exists a
`CellDecoder[A Xor B]`. If `A` and `B` each have a [`RowDecoder`], there exists a `RowDecoder[A Xor B]`.

First, a few imports:

```tut:silent
import cats.data.Xor
import kantan.csv.ops._
```

We can then simply write the following:

```tut
"1,2\n4,true".readCsv[List, (Int, Int Xor Boolean)](',', false)

"1,2\n4,true".readCsv[List, (Int, Int) Xor (Int, Boolean)](',', false)
```


## Cats instances

The following instance for cats type classes are provided:

* [`Functor`] for all decoders ([`CellDecoder`] and [`RowDecoder`]).
* [`Contravariant`] for all encoders ([`CellEncoder`] and [`RowEncoder`]).
* [`Order`] for all result types ([`ReadResult`], [`ParseResult`] and [`DecodeResult`]).
* [`Show`] for all result types.
* [`Monoid`] for all result types.
* [`Traverse`] for all result types.
* [`Monad`] for all result types.
* [`BiFunctor`] for all result types.

[`Functor`]:http://typelevel.org/cats/api/#cats.Functor
[`Contravariant`]:http://typelevel.org/cats/api/#cats.functor.Contravariant
[`BiFunctor`]:http://typelevel.org/cats/api/#cats.functor.Bifunctor
[`Order`]:http://typelevel.org/cats/api/index.html#cats.package@Order[A]=cats.kernel.Order[A]
[`Show`]:http://typelevel.org/cats/api/index.html#cats.Show
[`Traverse`]:http://typelevel.org/cats/api/index.html#cats.Traverse
[`Monad`]:http://typelevel.org/cats/api/index.html#cats.Monad
[`Xor`]:http://typelevel.org/cats/api/#cats.data.Xor
[`Monoid`]:http://typelevel.org/cats/api/index.html#cats.package@Monoid[A]=cats.kernel.Monoid[A]
[`CellEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`CellDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`RowDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`ReadResult`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@ReadResult[A]=kantan.codecs.Result[kantan.csv.ReadError,A]
[`ParseResult`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@ParseResult[A]=kantan.codecs.Result[kantan.csv.ParseError,A]
[`DecodeResult`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@DecodeResult[A]=kantan.codecs.Result[kantan.csv.DecodeError,A]
