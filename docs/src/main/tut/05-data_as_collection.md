---
layout: default
title:  "Deocding CSV data into a collection"
section: tutorial
---
When memory constraints are not an issue, decoding an entire CSV file into a single collection, such as a [`List`] or
[`Vector`], can be useful. kantan.csv offers simple primitives for just this purpose.

Let's take the cars example from [wikipedia](https://en.wikipedia.org/wiki/Comma-separated_values#Example):

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

Our goal here is to load this entire resource as a [`List`]. In order to do that, we must be able to decode each
row as a case class. This is exactly what we did in a [previous tutorial](03-rows_as_case_classes.html):

```tut:silent
import kantan.csv.ops._     // kantan.csv syntax
import kantan.csv.generic._ // case class decoder derivation

case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

Now that we have everything we need to decode the CSV data, here's how to turn it into a [`List`]:

```tut
rawData.readCsv[List, Car](',', true)
```

This [`readCsv`] method takes two type parameters: the type of the collection in which to store each row, and the type
as which to decode each row. In our example, we requested a [`List`] of `Car`, but we could just easily have asked
for a [`Set`] or a [`Vector`].

The two value parameters should be familiar by now: the first one is the column separator, the second one a flag to
let kantan.csv know whether to skip the first row or not.

Note that in our example, results are wrapped in a [`CsvResult`]. This allows [`readCsv`] to be safe - it does not throw
exceptions, preferring to encode errors in the return type instead. Should you rather not have to deal with errors and
let your code crash when they happen, you might prefer [`unsafeReadCsv`].

## Internals
Under the hood, this is done by using the standard [`CanBuildFrom`] type class: any collection for which there exists
a valid implementation of [`CanBuildFrom`] can be used as the first type parameter of [`readCsv`]. This should
essentially be all standard scala collections (with the exceptions of those that take more than one type parameter, such
as [`Map`], because of the way [`readCsv`] works).

The second type parameter is explained in an [earlier tutorial](01-rows_as_collections.html).

## What to read next
If you want to learn more about:

* [decoders and codecs](14-codecs.html)
* [how we were able to turn a `URI` into CSV data](07-csv_sources.html)
* [how to parse CSV row by row](06-step_by_step_parsing.html)


[`List`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List
[`Set`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Set
[`Map`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Map
[`Vector`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Vector
[`readCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvInputOps@readCsv[C[_],B](sep:Char,header:Boolean)(implicitevidence$5:kantan.csv.RowDecoder[B],implicitai:kantan.csv.CsvInput[A],implicitcbf:scala.collection.generic.CanBuildFrom[Nothing,kantan.csv.CsvResult[B],C[kantan.csv.CsvResult[B]]],implicite:kantan.csv.engine.ReaderEngine):C[kantan.csv.CsvResult[B]]
[`unsafeReadCsv`]:{{ site.baseurl }}/api/#kantan.csv.ops$$CsvInputOps@unsafeReadCsv[C[_],B](sep:Char,header:Boolean)(implicitevidence$6:kantan.csv.RowDecoder[B],implicitai:kantan.csv.CsvInput[A],implicitcbf:scala.collection.generic.CanBuildFrom[Nothing,B,C[B]],implicite:kantan.csv.engine.ReaderEngine):C[B]
[`CsvResult`]:{{ site.baseurl }}/api/#kantan.csv.package@CsvResult[A]=kantan.codecs.Result[kantan.csv.CsvError,A]
[`CanBuildFrom`]:http://www.scala-lang.org/api/current/index.html#scala.collection.generic.CanBuildFrom