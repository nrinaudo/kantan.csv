---
layout: tutorial
title: "Encoding arbitrary types as cells"
section: tutorial
sort_order: 11
---
We've seen in a [previous post](collections_as_rows.html) how to encode collections as CSV rows. Exactly *how* that
happened and how individual elements of the collection were turned into CSV cells was sort of glossed over, though. In
this tutorial, we'll take a deeper look at the underlying mechanism.

## General mechanism

Cell encoding is type class based: kantan.csv knows how to turn a type `A` into a CSV cell, provided that there is an
implicit instance of [`CellEncoder[A]`][`CellEncoder`] in scope. All sane primitive types have default implementations
 - `Int`, for example:

```scala mdoc
implicitly[kantan.csv.CellEncoder[Int]]
```

A more complete list of default instances can be found [here](default_instances.html)

And so, when [`asCsvWriter`], [`writeCsv`] or [`asCsv`] are asked to turn a collection of elements `A` into a CSV row,
it looks for a corresponding implicit [`CellEncoder`] and relies on it for encoding:

```scala mdoc
import kantan.csv._
import kantan.csv.ops._

List(List(1, 2, 3), List(4, 5, 6)).asCsv(rfc)
```

## Adding support to new types

In order to add support to non-standard types, all you need to do is implement an implicit [`CellEncoder`] instance for
that type. Let's do so, for example, for Joda [`DateTime`]:

```scala mdoc:silent
import kantan.csv._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val jodaDateTime: CellEncoder[DateTime] = {
  val format = ISODateTimeFormat.date()
  CellEncoder.from(format.print)
}
```

And we can now encode collections of dates:

```scala mdoc
List(
  List(new DateTime(), new DateTime().plusDays(1)),
  List(new DateTime().plusDays(2), new DateTime().plusDays(3))
).asCsv(rfc)
```

## What to read next
If you want to learn more about:

* [decoding arbitrary types](cells_as_arbitrary_types.html)
* [decoding rows as tuples](rows_as_tuples.html)
* [declaring decoders and encoders in a single call](codecs.html)


[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`asCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvRowsOps.html#asCsv(sep:Char,header:Seq[String])(implicitea:kantan.csv.RowEncoder[A],implicite:kantan.csv.engine.WriterEngine):String
[`writeCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#writeCsv[B](rows:TraversableOnce[B],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html
