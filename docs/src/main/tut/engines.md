---
layout: tutorial
title: "External CSV libraries"
section: tutorial
sort_order: 20
---
kantan.csv comes with a default implementation of CSV parsing and serialising. This implementation is
[relatively fast](benchmarks.html) and robust, but might not satisfy all use cases - some of the more outlandish CSV
mutations are not implemented (yet), for instance. For these cases, it's possible to use other CSV libraries under the
hood.

## Supported libraries

### Open CSV

The [opencsv] parser and serialiser can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-opencsv" % "0.1.14"
```

You then need to bring the right implicits in scope through:

```tut:silent
import kantan.csv.engine.opencsv._
```

### Commons CSV

The [commons csv] parser and serialiser can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-commons" % "0.1.14"
```

You then need to bring the right implicits in scope through:

```tut:silent
import kantan.csv.engine.commons._
```

### Jackson CSV

The [jackson csv] parser and serialiser can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-jackson" % "0.1.14"
```

You then need to bring the right implicits in scope through:

```tut:silent
import kantan.csv.engine.jackson._
```

## Supporting a new library

For the purpose of this tutorial, let's make up an hypothetical CSV library, EasyCSV, that provides the following:

```tut:silent
import java.io._

object EasyCSV {
  trait EasyWriter {
    def write(row: Array[String]): Unit
    def close(): Unit
  }

  def read(reader: Reader, sep: Char): java.util.Iterator[Array[String]] with Closeable = ???
  def write(writer: Writer, sep: Char): EasyWriter = ???
}
```


### Parsing

Support for parsing with external libraries is handled through the [`ReaderEngine`] trait: all functions that need
to create an instance of [`CsvReader`] rely on an implicit instance of [`ReaderEngine`] to do so.

Creating a new instance of [`ReaderEngine`] is meant to be fairly straightforward: there's a helper
[`ReaderEngine.apply`] method that takes care of this. It still means we need to be able to write a
`(Reader, Char) ⇒ CsvReader[Seq[String]]`, but the most common scenario is already covered: if you can write a
`(Reader, Char) ⇒ Iterator[Seq[String]]`, you can simply use [`ResourceIterator.fromIterator`]:

```tut:silent
import kantan.csv.engine._
import kantan.csv._

implicit val readerEngine = ReaderEngine { (in: Reader, sep: Char) ⇒
  kantan.codecs.resource.ResourceIterator.fromIterator(EasyCSV.read(in, sep))
}
```

### Serialising

Serialising is very similar to parsing, except that instead of providing a [`ReaderEngine`], you need to provide a
[`WriterEngine`]. This is achieved through [`WriterEngine.apply`], the argument to which you most likely want to create
through [`CsvWriter.apply`]:

```tut:silent
implicit val writerEngine = WriterEngine { (writer: Writer, sep: Char) ⇒
  CsvWriter(EasyCSV.write(writer, sep))(_ write _.toArray)(_.close())
}
```

[commons csv]:https://commons.apache.org/proper/commons-csv/
[jackson csv]:https://github.com/FasterXML/jackson-dataformat-csv
[opencsv]:http://opencsv.sourceforge.net
[`ReaderEngine`]:{{ site.baseurl }}/api/#kantan.csv.engine.ReaderEngine
[`WriterEngine`]:{{ site.baseurl }}/api/#kantan.csv.engine.WriterEngine
[`CsvReader`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CsvReader[A]=kantan.codecs.resource.ResourceIterator[A]
[`ReaderEngine.apply`]:{{ site.baseurl }}/api/#kantan.csv.engine.ReaderEngine$@apply(f:(java.io.Reader,Char)=>kantan.csv.CsvReader[kantan.csv.ReadResult[Seq[String]]]):kantan.csv.engine.ReaderEngine
[`CsvReader.fromResource`]:{{ site.baseurl }}/api/#kantan.csv.CsvReader$@fromResource[I,R](in:I)(open:I=>Iterator[R])(release:I=>Unit):kantan.csv.CsvReader[kantan.csv.ParseResult[R]]
[`WriterEngine.apply`]:{{ site.baseurl }}/api/#kantan.csv.engine.WriterEngine$@apply(f:(java.io.Writer,Char)=>kantan.csv.CsvWriter[Seq[String]]):kantan.csv.engine.WriterEngine
[`CsvWriter.apply`]:{{ site.baseurl }}/api/#kantan.csv.CsvWriter$@apply[A](writer:java.io.Writer,separator:Char,header:Seq[String])(implicitea:kantan.csv.RowEncoder[A],implicitengine:kantan.csv.engine.WriterEngine):kantan.csv.CsvWriter[A]
[`ResourceIterator.fromIterator`]:http://nrinaudo.github.io/kantan.codecs/api/index.html#kantan.codecs.ResourceIterator$@fromIterator[A](as:Iterator[A]):kantan.codecs.ResourceIterator[A]
