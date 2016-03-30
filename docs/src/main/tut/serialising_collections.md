---
layout: default
title:  "Encoding entire collections"
section: tutorial
sort: 14
---
While kantan.csv was written with large amount of data in mind - or at least, more data than a standard laptop can
comfortably fit in memory - it's still fairly common to have a collection that needs to be written down as CSV.

This is something that kantan.csv attempts to make as straightforward as possible. First, let's define some CSV data
that needs to be serialised (see [this](case_classes_as_rows) if you're not clear what the following code is for):

```tut:silent
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

All types that support the [`asCsvWriter`] method also support [`writeCsv`], which takes a collection of values and
writes them directly as CSV:

```tut:silent
// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

// Writes ps using , as a column separator and with a header row.
out.writeCsv(ps, ',', List("Id", "Name", "Age"))
```

[`writeCsv`] takes three value arguments:

* a collection (an instance of [`TraversableOnce`], really) to encode.
* a character to use as column separator
* an optional header row

Now that we have serialised our data, let's make sure it comes out the way we expected:

```tut
scala.io.Source.fromFile(out).mkString
```

Note that the need for turning a collection into a CSV string is so common that kantan.csv has a special helper for
that: [`asCsv`]. For example:

```tut
ps.asCsv(',', List("Id", "Name", "Age"))
```

[`asCsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`writeCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@writeCsv[B](rows:TraversableOnce[B],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`TraversableOnce`]:http://www.scala-lang.org/api/current/index.html#scala.collection.List
[`asCSv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$TraversableOnceOps@asCsv(sep:Char,header:Seq[String])(implicitengine:kantan.csv.engine.WriterEngine,implicitae:kantan.csv.RowEncoder[A]):String