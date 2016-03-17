---
layout: default
title:  "Decoding rows as tuples"
section: tutorial
---

In a [previous tutorial](01-rows_as_collections.html), we saw how to deal with CSV data composed of rows of homogeneous
types. While a common enough scenario, you'll also find yourself having to deal with heterogeneous data types fairly
often.

Take, for instance, the [wikipedia CSV example](https://en.wikipedia.org/wiki/Comma-separated_values#Example):

```
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

I have this data as a resource, so let's declare it:

```tut:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

One way of representing each row would be as a tuple. Let's declare it as a type alias, for brevity's sake:

```tut:silent
type Car = (Int, String, String, Option[String], Float)
```

kantan.csv has out of the box support for decoding tuples, so you can simply pass the corresponding type to
[`asCsvReader`]:

```tut:silent
import kantan.csv.ops._ // Brings in the kantan.csv syntax.

val reader = rawData.asCsvReader[Car](',', true)
```

And now that we have a [`CsvReader`] on the data, we can simply iterate through it:

```tut
reader.foreach(println _)
```

## What to read next
If you want to learn more about:

* [decoders and codecs](14-codecs.html)
* [how we were able to turn a `URI` into CSV data](07-csv_sources.html)
* [how to turn CSV rows into more useful types](03-rows_as_case_classes.html)
 

[`asCsvReader`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvInputOps@asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$3:kantan.csv.RowDecoder[B],implicitai:kantan.csv.CsvInput[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.CsvResult[B]]
[`CsvReader`]:{{ site.baseurl }}/api/#kantan.csv.CsvReader