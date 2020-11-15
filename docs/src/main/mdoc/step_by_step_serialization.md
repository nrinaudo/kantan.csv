---
layout: tutorial
title: "Encoding one row at a time"
section: tutorial
sort_order: 16
---
A [previous post](serializing_collections.html) covered how to encode entire collections as CSV. This is not,
however, always possible or even desirable: if the data to encode is obtained in a lazy way, it's usually better to
encoded it as it comes.

kantan.csv provides a useful class for this scenario: [`CsvWriter`], which can be thought of as a highly specialised
version of [`Writer`].

First, let's set up a type to encode as CSV (if the reason why the following code works is not clear, you can
[read up on it](case_classes_as_rows.html)).

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)
```

Obtaining an instance of [`CsvWriter`] is achieved by calling the [`asCsvWriter`] method that enriches any type that
has an implicit instance of [`CsvSink`] in scope - or, to keep things simple, any type that you expect to be able to
write CSV directly to. You can read up more on that mechanism [here](csv_sinks.html).

```scala mdoc:silent
// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

val writer = out.asCsvWriter[Person](rfc.withHeader("Id", "Name", "Age"))
```

Note the type parameter on [`asCsvWriter`], this is the type that our instance will be able to write.

Now that we have a [`CsvWriter`], we can just send instances of `Person` to its [`write`] method, and call [`close`]
when we're done:

```scala mdoc:silent
writer.write(Person(0, "Nicolas", 38)).
       write(Person(1, "Kazuma", 1)).
       write(Person(2, "John", 18)).
       close()
```

Note that this is still one of kantan.csv's rough edges. This code throws, and there currently is no alternative.
Future versions will fix that, but for the time being, you need to handle errors by yourself.

Let's make sure that we get the expected output:

```scala mdoc
scala.io.Source.fromFile(out).mkString
```

## What to read next
If you want to learn more about:

* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)

[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`CsvWriter`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html
[`CsvSink`]:{{ site.baseurl }}/api/kantan/csv/CsvSink.html
[`Writer`]:https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html
[`write`]:{{ site.baseurl }}/api/kantan/csv/CsvSink.html#write[A](s:S,rows:TraversableOnce[A],sep:Char,header:Seq[String])(implicitevidence$2:kantan.csv.RowEncoder[A],implicite:kantan.csv.engine.WriterEngine):Unit
[`close`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html#close():Unit
