---
layout: default
title:  "Parsing rows as collections"
section: tutorial
---

## Overview
A simple but very common type of CSV data is rows of numerical values, such as the following:

```
85.5, 54.0, 74.7, 34.2
63.0, 75.6, 46.8, 80.1
85.5, 39.6, 2.7, 38.7
```

I have this data as a resource, so let's make it available to the rest of this tutorial:

```tut:silent
val rawData: java.net.URL = getClass.getResource("/nums.csv")
```


kantan.csv makes it relatively simple to parse this. All we need to do is retrieve a [`CsvReader`] instance:

```tut:silent
import kantan.csv.ops._ // Brings in the kantan.csv syntax.

val reader = rawData.asCsvReader[List[Float]](',', false)
```

The [`asCsvReader`] scaladoc can seem a bit daunting with all its implicit parameters, so let's demystify it.

The first thing you'll notice is that it takes a type parameter, which is the type as which each row will be
interpreted. In our example, we requested each row to be parsed as a `List[Float]`.

The first value parameter, `,`, is the character that should be used as a column separator. It's usually `,`, but can
change - Excel, for instance, is infamous for using a system-dependent column separator.

Finally, the last value parameter is a boolean flat that, when set to `true`, will cause the first row to be skipped.
This is important for CSV data that contains a header row.

Now that we have our [`CsvReader`] instance, we can consume it - by, say, printing each row:

```tut
reader.foreach(println _)
```

Note that each result is wrapped in an instance of [`CsvResult`]. This allows parsing to be entirely safe - no exception
will be thrown, all error conditions are encoded at the type level. If safety is not a concern and you'd rather let
your code crash than deal with error conditions, you can use [`asUnsafeCsvReader`] instead.


## Internals
Parsing is type class based. Our example worked because kantan.csv can parse rows as collections of any type `A` such
that there is an implicit `CellDecoder[A]` in scope. Since all primitive types are supported by default, the compiler
was able to find a `CellDecoder[Float]`, and there was no further work required on your part.

While a lot of types are supported by default, you might need to add support for non-standard ones. A common example
is dates, which kantan.csv does not provide a default implementation for.

Adding support for Joda [`DateTime`] is rather straightforward: all you need is to implement a
[`CellDecoder[DateTime]`][`CellDecoder`].

```tut:silent
import kantan.csv._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val jodaDateTime: CellDecoder[DateTime] = {
  val format = ISODateTimeFormat.dateTime()
  CellDecoder(s ⇒ DecodeResult(format.parseDateTime(s)))
}
```

## What to read next
If you want to learn more about:

* [decoders and codecs](14-codecs.html)
* [how we were able to turn a `URI` into CSV data](06-csv_sources.html)
* [how to turn CSV rows into more useful types](03-rows_as_arbitrary_types.html)
 

[syntax]:{{ site.baseurl }}/api/#kantan.csv.ops$
[`CsvReader`]:{{ site.baseurl }}/api/#kantan.csv.CsvReader
[`CellDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CsvResult`]:{{ site.baseurl }}/api/#kantan.csv.package@CsvResult[A]=kantan.codecs.Result[kantan.csv.CsvError,A]
[`asCsvReader`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvInputOps@asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$3:kantan.csv.RowDecoder[B],implicitai:kantan.csv.CsvInput[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.CsvResult[B]]
[`asUnsafeCsvReader`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvInputOps@asUnsafeCsvReader[B](sep:Char,header:Boolean)(implicitevidence$4:kantan.csv.RowDecoder[B],implicitai:kantan.csv.CsvInput[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[B]
[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html