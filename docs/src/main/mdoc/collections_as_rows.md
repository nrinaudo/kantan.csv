---
layout: tutorial
title: "Encoding collections as rows"
section: tutorial
sort_order: 10
---
CSV is often used to store rows of homogeneous data - each cell could be an `Int`, for instance. We'll see in this post
how kantan.csv supports this kind of scenario.

In kantan.csv, all CSV serialization is done through [`CsvWriter`], which you can think of as a highly specialised
version of [`Writer`]. One retrieves an instance of [`CsvWriter`] as follows:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

val writer = out.asCsvWriter[List[Int]](rfc.withHeader("Column 1", "Column 2", "Column 3"))
```

Note the type parameter to [`asCsvWriter`]: this is what the returned instance of [`CsvWriter`] will know to encode.
Since we're trying to write lists of integers, we requested a [`CsvWriter[List[Int]]`][`CsvWriter`], but we could have
requested any subtype of [`TraversableOnce`] of any primitive Scala type.

The value parameters are the column separator and optional header row.

Now that we have a [`CsvWriter`] that knows how to encode [`List[Int]`][`List`], we can just call its [`write`] method
repeatedly:

```scala mdoc:silent
// Writes a couple of rows.
writer.write(List(0, 1, 2))
writer.write(List(3, 4, 5))

// Makes sure resources are freed.
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

* [encoding tuples as rows](tuples_as_rows.html)
* [encoding arbitrary types as cells](arbitrary_types_as_cells.html)
* [decoding rows from collections](rows_from_collections.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)


[`List`]:http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
[`CsvWriter`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html
[`Writer`]:https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html
[`write`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html#write(as:TraversableOnce[A]):kantan.csv.CsvWriter[A]
[`close`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html#close():Unit
[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`TraversableOnce`]:http://www.scala-lang.org/api/current/scala/collection/TraversableOnce.html
