---
layout: tutorial
title: "Decoding rows as collections"
section: tutorial
sort_order: 1
---
A simple but very common type of CSV data is rows of numerical values. This is something that kantan.csv tries to make
as as easy as possible to deal with.

First, we'll need some sample CSV data, which we'll get from this project's resources:

```scala mdoc:silent
val rawData: java.net.URL = getClass.getResource("/nums.csv")
```

This is what we're trying to parse:

```scala mdoc
scala.io.Source.fromURL(rawData).mkString
```

In order to turn this into useful types, all we need to do is retrieve a [`CsvReader`] instance:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._

val reader = rawData.asCsvReader[List[Float]](rfc)
```

The [`asCsvReader`] scaladoc can seem a bit daunting with all its implicit parameters, so let's demystify it.

The first thing you'll notice is that it takes a type parameter, which is the type into which each row will be
decoded. In our example, we requested each row to be decoded into a [`List[Float]`][`List`].

The first value parameter, `,`, is the character that should be used as a column separator. It's usually a comma, but
not all implementations agree on that - Excel, for instance, is infamous for using a system-dependent column separator.

Finally, the last value parameter is a boolean flag that, when set to `true`, will cause the first row to be skipped.
This is important for CSV data that contains a header row.

Now that we have our [`CsvReader`] instance, we can consume it - by, say, printing each row:

```scala mdoc
reader.foreach(println _)
```

Note that each result is wrapped in an instance of [`ReadResult`]. This allows decoding to be entirely safe - no
exception will be thrown, all error conditions are encoded at the type level. If safety is not a concern and you'd
rather let your code crash than deal with error conditions, you can use [`asUnsafeCsvReader`] instead.

Finally, observant readers might have noticed that we didn't bother closing the [`CsvReader`] - we're obviously dealing
with some sort of streamed resource, not closing it seems like a bug. In this specific case, however, it's not
necessary: [`CsvReader`] will automatically close any underlying resource when it's been consumed entirely, or a fatal
error occurs.

## What to read next
If you want to learn more about:

* [decoding rows as tuples](rows_as_tuples.html)
* [how `CsvReader` guessed how to turn CSV rows into `List[Float]` instances](cells_as_arbitrary_types.html)
* [encoding collections as rows](collections_as_rows.html)
* [how we were able to turn a `URL` into CSV data](csv_sources.html)

[`List`]:http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
[`CsvReader`]:{{ site.baseurl }}/api/kantan/csv/package$$CsvReader.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`ReadResult`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html
[`asCsvReader`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$1:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.ReadResult[B]]
[`asUnsafeCsvReader`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#asUnsafeCsvReader[B](sep:Char,header:Boolean)(implicitevidence$2:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[B]
