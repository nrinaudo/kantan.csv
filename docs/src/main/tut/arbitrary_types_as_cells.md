---
layout: default
title:  "Encoding arbitrary types as cells"
section: tutorial
sort: 11
---
We've seen in a [previous post](collections_as_rows.html) how to encode collections as CSV rows. Exactly *how* that
happened and how individual elements of the collection were turned into CSV cells was sort of glossed over, though. In 
this tutorial, we'll take a deeper look at the underlying mechanism.

## General mechanism

Cell encoding is type class based: kantan.csv knows how to turn a type `A` into a CSV cell, provided that there is an
implicit instance of [`CellEncoder[A]`][`CellEncoder`] in scope. All sane primitive types have a default implementation
in scope, which we can check by attempting to summon them:

```tut
import kantan.csv._

implicitly[CellEncoder[Int]]
implicitly[CellEncoder[Float]]
implicitly[CellEncoder[Boolean]]
```

And so, when [`asCsvWriter`], [`writeCsv`] or [`asCsv`] are asked to turn a collection of elements `A` into a CSV row,
it looks for a corresponding implicit [`CellEncoder`] and relies on it for encoding:

```tut
import kantan.csv.ops._

List(List(1, 2, 3), List(4, 5, 6)).asCsv(',')
```

## Adding support to new types

In order to add support to non-standard types, all you need to do is implement an implicit [`CellEncoder`] instance for
that type. Let's do so, for example, for Joda [`DateTime`]:
 
```tut:silent
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val jodaDateTime: CellEncoder[DateTime] = {
  val format = ISODateTimeFormat.date()
  CellEncoder(d ⇒ format.print(d))
}
```

And we can now encode collections of dates:

```tut
List(
  List(new DateTime(), new DateTime().plusDays(1)),
  List(new DateTime().plusDays(2), new DateTime().plusDays(3))
).asCsv(',')
```

[`asCsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`asCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$TraversableOnceOps@asCsv(sep:Char,header:Seq[String])(implicitengine:kantan.csv.engine.WriterEngine,implicitae:kantan.csv.RowEncoder[A]):String
[`writeCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@writeCsv[B](rows:TraversableOnce[B],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`CellEncoder`]:{{ site.baseurl }}/api/#kantan.csv.CellEncoder$
[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html