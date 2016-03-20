---
layout: default
title:  "Encoding tuples as rows"
section: tutorial
---
In a [previous post](09-collections_as_rows.html), we've seen how to encode collections as CSV rows. This is useful when
dealing with homogeneous data, but not so much when not all cells in a given row are of the same time. One way of
dealing with such situation is tuples.

Let's imagine that we have data composed of an `Int`, a `Float` and a `Boolean`, and that we need to turn that into
a CSV file.

The first step is to retrieve a [`CsvWriter`] instance on the desired file:

```tut:silent
import kantan.csv.ops._

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

// Writer on `out`
val writer = out.asCsvWriter[(Int, Float, Boolean)](',', List("Column 1", "Column 2", "Column 3"))
```

Note the type parameter to [`asCsvWriter`]: this is what the returned instance of [`CsvWriter`] will know to encode.
We're trying to write instances of `(Int, Float, Boolean)`, which is what we passed. 

The value parameters are the column separator and optional header row.

Now that we have a [`CsvWriter`] that knows how to encode `(Int, Float, Boolean)`, we can just call its [`write`] method
repeatedly:

```tut:silent
writer.write((0, 1F, false))
writer.write((3, 4F, true))
writer.close()
```

Let's make sure that we got the expected output:

```tut
scala.io.Source.fromFile(out).mkString
```


[`CsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.CsvWriter
[`asCsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`write`]:{{ site.baseurl }}/api/#kantan.csv.CsvWriter@write(a:A):kantan.csv.CsvWriter[A]