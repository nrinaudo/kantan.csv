---
layout: tutorial
title: "Encoding case classes as rows"
section: tutorial
sort_order: 13
---

In a [previous post](tuples_as_rows.html), we've seen how to encode tuples as CSV rows. While useful, actual code
rarely stores business objects as tuples - encoding case classes is a much more common need than encoding tuples.

Let's imagine that we have a list of values of type `Person` to encode:

```scala
final case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

If we find ourselves in the easy case - that is, we don't mind a [shapeless] dependency *and* the expected order of
cells in the output CSV matches exactly the order of fields in our case class, we can just let the compiler work out
how to do that.

You'll first need to add a dependency to the [generic](shapeless.html) module in your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1-SNAPSHOT"
```

Then, with the appropriate imports:

```scala
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

val writer = out.asCsvWriter[Person](rfc.withHeader("Column 1", "Column 2", "Column 3"))
```

We're dealing with a list of values, which [`CsvWriter`] as a helper method for which we haven't seen before:

```scala
writer.write(ps).close()
```

And, through the magic of automatic type class instance derivation, everything worked out as expected:

```scala
scala.io.Source.fromFile(out).mkString
// res1: String = """Column 1,Column 2,Column 3
// 0,Nicolas,38
// 1,Kazuma,1
// 2,John,18
// """
```

This was the easy case though. A more complicated one is if we need to, say, encode fields in a different order than
the one they were declared in the class. For this, we need to go a little bit closer to the metal and declare our own
[`RowEncoder`].

Luckily, case classes are such a common case that kantan.csv has all sorts of helpers for them - in our specific case,
we're looking for the [`caseEncoder`] method of object [`RowEncoder`], which simply takes a list of integers mapping
fields to their index in the CSV row and the case class' `unapply` method:

```scala
val personEncoder: RowEncoder[Person] = RowEncoder.caseEncoder(0, 2, 1)(Person.unapply)
```

## What to read next

If you want to learn more about:

* [encoding arbitrary types as rows](arbitrary_types_as_rows.html)
* [decoding rows as case classes](rows_as_case_classes.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)

[shapeless]:https://github.com/milessabin/shapeless

[`CsvWriter`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`caseEncoder`]:{{ site.baseurl }}/api/kantan/csv/GeneratedRowEncoders.html#caseEncoder[C,A1,A2,A3,A4](i1:Int,i2:Int,i3:Int,i4:Int)(f:C=>Option[(A1,A2,A3,A4)])(implicitevidence$513:kantan.csv.CellEncoder[A1],implicitevidence$514:kantan.csv.CellEncoder[A2],implicitevidence$515:kantan.csv.CellEncoder[A3],implicitevidence$516:kantan.csv.CellEncoder[A4]):kantan.csv.RowEncoder[C]
[`TraversableOnce`]:http://www.scala-lang.org/api/current/scala/collection/TraversableOnce.html
[`asCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvRowsOps.html#asCsv(sep:Char,header:Seq[String])(implicitea:kantan.csv.RowEncoder[A],implicite:kantan.csv.engine.WriterEngine):String
