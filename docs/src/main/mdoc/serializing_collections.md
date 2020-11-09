---
layout: tutorial
title: "Encoding entire collections"
section: tutorial
sort_order: 15
---
While kantan.csv was written with large amount of data in mind - or at least, more data than a standard laptop can
comfortably fit in memory - it's still fairly common to have a collection that needs to be written down as CSV.

This is something that kantan.csv attempts to make as straightforward as possible. First, let's define some CSV data
that needs to be serialized (see [this](case_classes_as_rows) if you're not clear what the following code is for):

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

All types that support the [`asCsvWriter`] method also support [`writeCsv`], which takes a collection of values and
writes them directly as CSV:

```scala mdoc:silent
// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

// Writes ps using , as a column separator and with a header row.
out.writeCsv(ps, rfc.withHeader("Id", "Name", "Age"))
```

[`writeCsv`] takes three value arguments:

* a collection (an instance of [`TraversableOnce`], really) to encode.
* a character to use as column separator
* an optional header row

Now that we have serialized our data, let's make sure it comes out the way we expected:

```scala mdoc
scala.io.Source.fromFile(out).mkString
```

Note that the need for turning a collection into a CSV string is so common that kantan.csv has a special helper for
that: [`asCsv`]. For example:

```scala mdoc
ps.asCsv(rfc.withHeader("Id", "Name", "Age"))
```

## What to read next

If you want to learn more about:

* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)
* [how to write CSV row by row](step_by_step_serialization.html)


[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`writeCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#writeCsv[B](rows:TraversableOnce[B],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`TraversableOnce`]:http://www.scala-lang.org/api/current/scala/collection/TraversableOnce.html
[`asCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvRowsOps.html#asCsv(sep:Char,header:Seq[String])(implicitea:kantan.csv.RowEncoder[A],implicite:kantan.csv.engine.WriterEngine):String
