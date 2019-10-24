---
layout: tutorial
title: "Encoding tuples as rows"
section: tutorial
sort_order: 12
---
In a [previous post](collections_as_rows.html), we've seen how to encode collections as CSV rows. This is useful when
dealing with homogeneous data, but not so much when not all cells in a given row are of the same time. One way of
dealing with such situation is tuples.

Let's imagine that we have data composed of an `Int`, a `Float` and a `Boolean`, and that we need to turn that into
a CSV file.

The first step is to retrieve a [`CsvWriter`] instance on the desired file:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

val writer = out.asCsvWriter[(Int, Float, Boolean)](rfc.withHeader("Column 1", "Column 2", "Column 3"))
```

Note the type parameter to [`asCsvWriter`]: this is what the returned instance of [`CsvWriter`] will know to encode.
We're trying to write instances of `(Int, Float, Boolean)`, which is what we passed.

The value parameters are the column separator and optional header row.

Now that we have a [`CsvWriter`] that knows how to encode `(Int, Float, Boolean)`, we can just call its [`write`] method
repeatedly:

```scala mdoc:silent
writer.write((0, 1F, false))
writer.write((3, 4F, true))
writer.close()
```

Note that [`CsvWriter`] supports a fluent style of chaining operations (or, in less pretentious english, each [`write`]
returns the [`CsvWriter`] itself), which I could have used to simplify the above code. I feel that spelling things out
helps, though, especially the explicit call to [`close`] at the end.

Let's make sure that we got the expected output:

```scala mdoc
scala.io.Source.fromFile(out).mkString
```

## What to read next
If you want to learn more about:

* [encoding case classes as rows](case_classes_as_rows.html)
* [decoding rows as tuples](rows_as_typles.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)


[`CsvWriter`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html
[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`write`]:{{ site.baseurl }}/api/kantan/csv/CsvSink.html#write[A](s:S,rows:TraversableOnce[A],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`close`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html#close():Unit
