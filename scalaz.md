---
layout: scala mdocorial
title: "Scalaz module"
section: scala mdocorial
sort_order: 24
---

Kantan.csv has a [scalaz](https://github.com/scalaz/scalaz) module that is, in its current incarnation, fairly bare
bones: it provides decoders for [`Maybe`] and [`\/`] as well as a few useful type class instances.

The `scalaz` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-scalaz" % "0.6.1-SNAPSHOT"
```

You then need to import the corresponding package:

```scala
import kantan.csv.scalaz._
```

## `\/` codecs

For any two types `A` and `B` that each have a [`CellEncoder`], there exists a
`CellEncoder[A \/ B]`. If `A` and `B` each have a [`RowEncoder`], there exists a `RowEncoder[A \/ B]`.

By the same token, for any two types `A` and `B` that each have a [`CellDecoder`], there exists a
`CellDecoder[A \/ B]`. If `A` and `B` each have a [`RowDecoder`], there exists a `RowDecoder[A \/ B]`.

First, a few imports:

```scala
import scalaz._
import kantan.csv._
import kantan.csv.ops._
```

We can then simply write the following:

```scala
"1,2\n4,true".readCsv[List, (Int, Int \/ Boolean)](rfc)
// res0: List[ReadResult[(Int, Int \/ Boolean)]] = List(
//   Right((1, -\/(2))),
//   Right((4, \/-(true)))
// )

"1,2\n4,true".readCsv[List, (Int, Int) \/ (Int, Boolean)](rfc)
// res1: List[ReadResult[(Int, Int) \/ (Int, Boolean)]] = List(
//   Right(-\/((1, 2))),
//   Right(\/-((4, true)))
// )
```


## `Maybe` decoder

For any type `A` that has:

* a [`CellDecoder`], there exists a `CellDecoder[Maybe[A]]`
* a [`RowDecoder`], there exists a `RowDecoder[Maybe[A]]`
* a [`CellEncoder`], there exists a `CellEncoder[Maybe[A]]`
* a [`RowEncoder`], there exists a `RowEncoder[Maybe[A]]`

You can write, for example:

```scala
"1,2\n3,".readCsv[List, (Int, Maybe[Int])](rfc)
// res2: List[ReadResult[(Int, Maybe[Int])]] = List(
//   Right((1, Just(2))),
//   Right((3, Empty()))
// )
```

## Scalaz instances

The following instance for cats type classes are provided:

* [`MonadError`] and [`Plus`] for all decoders ([`CellDecoder`] and [`RowDecoder`]).
* [`Contravariant`] for all encoders ([`CellEncoder`] and [`RowEncoder`]).
* [`Show`] and [`Equal`] for all error types ([`ReadError`] and all its descendants).
* [`RowEncoder`] for any type that has a [`Foldable`].

[`MonadError`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/MonadError.html
[`Plus`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Plus.html
[`Show`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Show.html
[`Equal`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Equal.html
[`Foldable`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Foldable.html
[`\/`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/$bslash$div.html
[`Maybe`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Maybe.html

[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/RowDecoder$.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`ReadError`]:{{ site.baseurl }}/api/kantan/csv/ReadError.html
[`Contravariant`]:https://static.javadoc.io/org.scalaz/scalaz_2.12/7.2.18/scalaz/Contravariant.html
