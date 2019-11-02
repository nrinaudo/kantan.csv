---
layout: tutorial
title: "Decoding CSV data into a collection"
section: tutorial
sort_order: 6
---
When memory constraints are not an issue, decoding an entire CSV file into a single collection, such as a [`List`] or
[`Vector`], can be useful. kantan.csv offers simple primitives for just this purpose.


Let's take the cars example from [wikipedia](https://en.wikipedia.org/wiki/Comma-separated_values#Example), which
we'll get from this project's resources:

```scala mdoc:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```scala mdoc
scala.io.Source.fromURL(rawData).mkString
```

Our goal here is to load this entire resource as a [`List`]. In order to do that, we must be able to decode each
row as a case class. This is exactly what we did in a [previous tutorial](rows_as_case_classes.html):

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

Now that we have everything we need to decode the CSV data, here's how to turn it into a [`List`]:

```scala mdoc
rawData.readCsv[List, Car](rfc.withHeader)
```

This [`readCsv`] method takes two type parameters: the type of the collection in which to store each row, and the type
as which to decode each row. In our example, we requested a [`List`] of `Car`, but we could just easily have asked
for a [`Set`] or a [`Vector`] - it could, in fact, have been anything that has a [`CanBuildFrom`] instance.

The two value parameters should be familiar by now: the first one is the column separator, the second one a flag to
let kantan.csv know whether to skip the first row or not.

Note that in our example, results are wrapped in a [`ReadResult`]. This allows [`readCsv`] to be safe - it does not throw
exceptions, preferring to encode errors in the return type instead. Should you rather not have to deal with errors and
let your code crash when they happen, you might prefer [`unsafeReadCsv`].


## What to read next

If you want to learn more about:

* [how we were able to turn a `URI` into CSV data](csv_sources.html)
* [how to parse CSV row by row](step_by_step_parsing.html)


[`List`]:http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
[`Set`]:http://www.scala-lang.org/api/current/scala/collection/Set.html
[`Map`]:http://www.scala-lang.org/api/current/scala/collection/immutable/Map.html
[`Vector`]:http://www.scala-lang.org/api/current/scala/collection/immutable/Vector.html
[`readCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#readCsv[C[_],B](sep:Char,header:Boolean)(implicitevidence$3:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine,implicitcbf:scala.collection.generic.CanBuildFrom[Nothing,kantan.csv.ReadResult[B],C[kantan.csv.ReadResult[B]]]):C[kantan.csv.ReadResult[B]]
[`unsafeReadCsv`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#unsafeReadCsv[C[_],B](sep:Char,header:Boolean)(implicitevidence$4:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine,implicitcbf:scala.collection.generic.CanBuildFrom[Nothing,B,C[B]]):C[B]
[`ReadResult`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html
[`CanBuildFrom`]:http://www.scala-lang.org/api/current/scala/collection/generic/CanBuildFrom.html
