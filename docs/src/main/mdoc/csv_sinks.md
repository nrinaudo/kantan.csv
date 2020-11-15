---
layout: tutorial
title: "What can CSV data be written to?"
section: tutorial
sort_order: 17
---
All the encoding tutorials, such as [this](serializing_collections.html) one, matter-of-factly call the
[`asCsvWriter`] method of [`File`], when [`File`] does not in fact have such a method. This works thanks to the
[`CsvSink`] type class.

What happens is, any type `A` such that there exists an implicit instance of [`CsvSink[A]`][`CsvSink`] in scope will
be enriched with various [useful methods]({{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html) for CSV serialization.
Various default implementations are automatically in scope, such as one for [`Writer`] or [`OutputStream`], but the most
useful one is [`File`].

## Implementation from scratch

Reduced to its simplest expression, a [`CsvSink`] is essentially a `A => Writer` - that is, a function that takes an
`A` and turns it into a [`Writer`] (note that this currently does not leave room for errors and implementations are
forced to throw. This is something that will be fixed in later releases).

If you can write such a function, you can trivially turn it into a valid instance of [`CsvSink`] - for example,
[`File`]:

```scala mdoc:silent
import kantan.csv._
import java.io._
import scala.io._

implicit def fileOutput(implicit c: Codec): CsvSink[File] =
  CsvSink.from(f => new OutputStreamWriter(new FileOutputStream(f), c.charSet))
```

## Adapting existing instances

Most of the time though, it's easier to turn the type you wish to provide an instance for into a type that already has
an instance - in our example, [`File`] can easily be turned into an [`OutputStream`], and [`OutputStream`] already has
a [`CsvSink`] instance. This is achieved through [`contramap`]:

```scala mdoc:reset:silent
import kantan.csv._
import java.io._

implicit val fileOutput: CsvSink[File] =
  CsvSink[OutputStream].contramap(f => new FileOutputStream(f))
```


[`asCsvWriter`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSinkOps.html#asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvSink[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`CsvWriter`]:{{ site.baseurl }}/api/kantan/csv/CsvWriter.html
[`CsvSink`]:{{ site.baseurl }}/api/kantan/csv/CsvSink.html
[`Writer`]:https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html
[`File`]:https://docs.oracle.com/javase/7/docs/api/java/io/File.html
[`OutputStream`]:https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html
[`contramap`]:{{ site.baseurl }}/api/kantan/csv/CsvSink.html#contramap[T](f:T=>S):kantan.csv.CsvSink[T]
