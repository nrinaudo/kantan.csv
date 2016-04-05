---
layout: default
title: "Encoding case classes as rows"
section: tutorial
sort: 13
---
In a [previous post](tuples_as_rows.html), we've seen how to encode tuples as CSV rows. While useful, actual code
rarely stores business objects as tuples - encoding case classes is a much more common need than encoding tuples.

Let's imagine that we have a list of values of type `Person` to encode:

```tut:silent
case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

If we find ourselves in the easy case - that is, we don't mind a [shapeless] dependency *and* the expected order of
cells in the output CSV matches exactly the order of fields in our case class, we can just let the compiler work out
how to do that:

```tut:silent
import kantan.csv.ops._
import kantan.csv.generic._

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

val writer = out.asCsvWriter[Person](',', List("Column 1", "Column 2", "Column 3"))
```

We're dealing with a list of values, which [`CsvWriter`] as a helper method for which we haven't seen before:

```tut:silent
writer.write(ps).close()
```

And, through the magic of automatic type class instance derivation, everything worked out as expected:

```tut
scala.io.Source.fromFile(out).mkString
```

This was the easy case though. A more complicated one is if we need to, say, encode fields in a different order than
the one they were declared in the class. For this, we need to go a little bit closer to the metal and declare our own
[`RowEncoder`].

Luckily, case classes are such a common case that kantan.csv has all sorts of helpers for them - in our specific case,
we're looking for the [`caseEncoderXXX`] method of object [`RowEncoder`], where `XXX` is the number of fields in the
class. This method simply takes the case class' `unapply` method and a list of integers mapping fields to their index
in the CSV row:

```tut:silent
import kantan.csv._
implicit val personEncoder: RowEncoder[Person] = RowEncoder.caseEncoder3(Person.unapply)(0, 2, 1) 
```

And to check whether that worked out, let's use another helper function: most Scala collections (all subtypes of
[`TraversableOnce`] are augmented with an [`asCsv`] method):

```tut
ps.asCsv(',')
```

## What to read next

If you want to learn more about:

* [encoding arbitrary types as rows](arbitrary_types_as_rows.html)
* [decoding rows as case classes](rows_as_case_classes.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)

[shapeless]:https://github.com/milessabin/shapeless
[`CsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.CsvWriter
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`caseEncoderXXX`]:{{ site.baseurl }}/api/#kantan.csv.RowEncoder$@caseEncoder3[C,A1,A2,A3](f:C=>Option[(A1,A2,A3)])(i1:Int,i2:Int,i3:Int)(implicitevidence$5:kantan.csv.CellEncoder[A1],implicitevidence$6:kantan.csv.CellEncoder[A2],implicitevidence$7:kantan.csv.CellEncoder[A3]):kantan.csv.RowEncoder[C]
[`TraversableOnce`]:http://www.scala-lang.org/api/current/index.html#scala.collection.List
[`asCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$TraversableOnceOps@asCsv(sep:Char,header:Seq[String])(implicitengine:kantan.csv.engine.WriterEngine,implicitae:kantan.csv.RowEncoder[A]):String