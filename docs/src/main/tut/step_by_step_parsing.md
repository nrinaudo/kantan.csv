---
layout: tutorial
title: "Decoding CSV data one row at a time"
section: tutorial
sort_order: 7
---
CSV data is sometimes unreasonably large - I've had to deal with CSV files in the multiple gigabytes - and cannot
comfortably fit in memory. It's better to treat these cases as an iterator of sorts, which is kantan.csv's default
mode of operation.

Let's take the by now familiar cars example from
[wikipedia](https://en.wikipedia.org/wiki/Comma-separated_values#Example), available in this project's resources:

```tut:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```tut
scala.io.Source.fromURL(rawData).mkString
```

Our goal here is to parse this resource row by row. In order to do that, we must be able to decode each
row as a case class. This is exactly what we did in a [previous tutorial](rows_as_case_classes.html):

```tut:silent
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

Now that we have everything we need to decode the CSV data, here's how to turn it into something that is essentially
an iterator with a `close` method:

```tut
val iterator = rawData.asCsvReader[Car](CsvConfiguration.default, true)
```

[`asCsvReader`] is explained in some depths [here](rows_as_collections.html), but we're more interested in what we
can do with our [`CsvReader`].

The first, fairly important thing we can do is `close` it if we don't intend to read the whole thing. If we do,
however, it will happen automatically and needs not be done explicitly.

Other than that, it looks a lot like any other standard collection. And being an iterator, it's lazy: you can apply
multiple `filter` and `map` operations, and nothing will happen until each row is explicitly requested. For example:

```tut
val filtered = iterator.filter(_.exists(_.year >= 1997)).map(_.map(_.make))
```

Note that this is a bit cumbersome - our iterator contains [`ReadResult[Car]`][`ReadResult`], which forces us to use
two levels of filtering / mapping. [`CsvReaderOps`] provides more comfortable alternatives:

```tut
val filtered = iterator.filterResult(_.year >= 1997).mapResult(_.make)
```

At this point, no data has been parsed yet. We can now, say, take the first element:

```tut
filtered.next
```

And this will only read as much as it needs to decode that first row. You could iterate over huge CSV files this way
without loading more than one row at a time in memory.

## What to read next
If you want to learn more about:

* [how we were able to turn a `URL` into CSV data](csv_sources.html)
* [error handling when parsing CSV data](error_handling.html)


[`asCsvReader`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$1:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.ReadResult[B]]
[`CsvReader`]:{{ site.baseurl }}/api/kantan/csv/index.html#CsvReader[A]=kantan.codecs.resource.ResourceIterator[A]
[`CsvReaderOps`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvReaderOps.html
[`Set`]:http://www.scala-lang.org/api/current/scala/collection/Set.html
[`ReadResult`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html
