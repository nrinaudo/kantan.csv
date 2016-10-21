---
layout: tutorial
title: "Decoding cells as arbitrary types"
section: tutorial
sort_order: 2
---
We've seen in a [previous post](rows_as_collections.html) how to decode CSV rows as collections. Exactly *how* that
happened and how individual cells were turned into useful types was sort of glossed over, though. In this tutorial,
we'll take a deeper look at the underlying mechanism.

## General mechanism

Cell decoding is type class based: kantan.csv knows how to turn a CSV cell into a type `A`, provided that there is an
implicit instance of [`CellDecoder[A]`][`CellDecoder`] in scope. All sane primitive types have a default implementation
in scope - `Int`, for example:

```tut
implicitly[kantan.csv.CellDecoder[Int]]
```

A more complete list of default instances can be found [here](default_instances.html).

And so, when [`asCsvReader`] or [`readCsv`] are asked to turn a row into a [`List`] of elements `A`, they look for a
corresponding implicit [`CellDecoder[A]`][`CellDecoder`] and rely on it for decoding:

```tut
import kantan.csv.ops._

"1,2,3\n4,5,6".readCsv[List, List[Int]](',', false)
```

## Adding support to new types

In order to add support to non-standard types, all you need to do is implement an implicit [`CellDecoder`] instance for
that type. Let's do so, for example, for Joda [`DateTime`]:

```tut:silent
import kantan.csv._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val jodaDateTime: CellDecoder[DateTime] = {
  val format = ISODateTimeFormat.date()
  CellDecoder.from(s ⇒ DecodeResult(format.parseDateTime(s)))
}
```

And we can now decode CSV data composed of dates:

```tut
"2009-01-06,2009-01-07\n2009-01-08,2009-01-09".asCsvReader[List[DateTime]](',', false).foreach(println _)
```

## What to read next
If you want to learn more about:

* [encoding arbitrary types](arbitrary_types_as_cells.html)
* [encoding tuples as rows](tuples_as_rows.html)
* [declaring decoders and encoders in a single call](codecs.html)

[`CellDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package$$CellDecoder
[`asCsvReader`]:{{ site.baseurl }}/api/index.html#kantan.csv.ops.CsvSourceOps@asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$1:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.ReadResult[B]]
[`readCsv`]:{{ site.baseurl }}/api/index.html#kantan.csv.ops.CsvSourceOps@readCsv[C[_],B](sep:Char,header:Boolean)(implicitevidence$3:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine,implicitcbf:scala.collection.generic.CanBuildFrom[Nothing,kantan.csv.ReadResult[B],C[kantan.csv.ReadResult[B]]]):C[kantan.csv.ReadResult[B]]
[`List`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List
[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html
