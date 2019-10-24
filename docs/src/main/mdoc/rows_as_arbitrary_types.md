---
layout: tutorial
title: "Decoding rows as arbitrary types"
section: tutorial
sort_order: 5
---
Other tutorials covered decoding rows as [collections](rows_as_collections.html), [tuples](rows_as_tuples.html)
and [case classes](rows_as_case_classes.html). While those are the most common scenarios, it is sometimes necessary
to decode rows into types that are none of these.

Let's take the same example we did before:

```scala mdoc:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```scala mdoc
scala.io.Source.fromURL(rawData).mkString
```

Now, let's imagine we have a class that is not a case class into which we'd like to decode each row:

```scala mdoc:silent
class Car(val year: Int, val make: String, val model: String, val desc: Option[String], val price: Float) {
  override def toString = s"Car($year, $make, $model, $desc, $price)"
}
```

Providing decoding support for our `Car` class is as simple as implementing an instance of [`RowDecoder`] for it
and marking it as implicit.

There are various ways to implement an instance of [`RowDecoder`], but by far the most idiomatic is to use one of
the various helper methods defined in its [companion object]({{ site.baseurl }}/api/kantan/csv/RowDecoder$.html). For
our current task, we need to decode a row into 5 values and stick them into `Car`'s constructor: we want [`ordered`].

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._

implicit val carDecoder: RowDecoder[Car] = RowDecoder.ordered { (i: Int, ma: String, mo: String, d: Option[String], p: Float) =>
  new Car(i, ma, mo, d, p)
}
```

And we can now decode our data as usual:

```scala mdoc
rawData.asCsvReader[Car](rfc.withHeader).foreach(println _)
```

The main reason this is the preferred solution is that it allows us never to have to think about individual cells in a
row and how to decode them - we just have to describe what type we're expecting and let kantan.csv deal with decoding
for us.

Note that this case was fairly simple - the column and constructor parameters were in the same order. For more complex
scenarios, where columns might be in a different order for example, [`decoder`] would be a better fit.

## What to read next

If you want to learn more about:

* [decoding entire CSV files in one go](data_as_collection.html)
* [encoding arbitrary types as rows](arbitrary_types_as_rows.html)
* [how we were able to turn a `URL` into CSV data](csv_sources.html)

[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
[`decoder`]:{{ site.baseurl }}/api/kantan/csv/RowDecoder$.html#decoder[A1,A2,A3,A4,R](i1:Int,i2:Int,i3:Int,i4:Int)(f:(A1,A2,A3,A4)=>R)(implicitevidence$7:kantan.csv.CellDecoder[A1],implicitevidence$8:kantan.csv.CellDecoder[A2],implicitevidence$9:kantan.csv.CellDecoder[A3],implicitevidence$10:kantan.csv.CellDecoder[A4]):kantan.csv.RowDecoder[R]
[`ordered`]:{{ site.baseurl }}/api/kantan/csv/GeneratedRowDecoders.html#ordered[A1,A2,A3,A4,A5,R](f:(A1,A2,A3,A4,A5)=>R)(implicitevidence$264:kantan.csv.CellDecoder[A1],implicitevidence$265:kantan.csv.CellDecoder[A2],implicitevidence$266:kantan.csv.CellDecoder[A3],implicitevidence$267:kantan.csv.CellDecoder[A4],implicitevidence$268:kantan.csv.CellDecoder[A5]):kantan.csv.RowDecoder[R]
