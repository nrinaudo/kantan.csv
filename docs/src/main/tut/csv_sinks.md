---
layout: default
title:  "What can CSV data be written to?"
section: tutorial
sort: 17
---
All the encoding tutorials, such as [this](serialising_collections.html) one, matter-of-factly call the
[`asCsvWriter`] method of [`File`], when [`File`] does not in fact have such a method. This works thanks to the
[`CsvOutput`] type class.

What happens is, any type `A` such that there exists an implicit instance of `CsvOutput[A]` in scope will be enriched
with various [useful methods]({{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps) for CSV serialisation. Various
default implementations are automatically in scope, such as one for [`Writer`] or [`OutputStream`], but the most
useful one is [`File`].

## Implementation from scratch

Reduced to its simplest expression, a [`CsvOutput`] is essentially a `A ⇒ Writer` - that is, a function that takes an 
`A` and turns it into a [`Writer`] (note that this currently does not leave room for errors and implementations are
forced to throw. This is something that will be fixed in later releases).

If you can write such a function, you can trivially turn it into a valid instance of [`CsvOutput`] - for example,
[`File`]:

```tut:silent
import kantan.csv._
import java.io._
import scala.io._

implicit def fileOutput(implicit c: Codec): CsvOutput[File] =
  CsvOutput(f ⇒ new OutputStreamWriter(new FileOutputStream(f), c.charSet)) 
```

## Adapting existing instances

Most of the time though, it's easier to turn the type you wish to provide an instance for into a type that already has
an instance - in our example, [`File`] can easily be turned into an [`OutputStream`], and [`OutputStream`] already has
a [`CsvOutput`] instance. This is achieved through [`contramap`]:

```tut:silent
implicit def fileOutput(implicit c: Codec): CsvOutput[File] =
  CsvOutput[OutputStream].contramap(f ⇒ new FileOutputStream(f)) 
```


[`asCsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvOutputOps@asCsvWriter[B](sep:Char,header:Seq[String])(implicitevidence$1:kantan.csv.RowEncoder[B],implicitoa:kantan.csv.CsvOutput[A],implicite:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[B]
[`CsvWriter`]:{{ site.baseurl }}/api/#kantan.csv.CsvWriter
[`CsvOutput`]:{{ site.baseurl }}/api/#kantan.csv.CsvOutput
[`Writer`]:https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html
[`File`]:https://docs.oracle.com/javase/7/docs/api/java/io/File.html
[`OutputStream`]:https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html
[`contramap`]:{{ site.baseurl }}/api/#kantan.csv.CsvOutput@contramap[T](f:T=>S):kantan.csv.CsvOutput[T]