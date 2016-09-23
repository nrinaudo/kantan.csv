---
layout: tutorial
title: "Decoding rows as case classes"
section: tutorial
sort_order: 4
---

## Overview
In a [previous tutorial](rows_as_tuples), we saw how to decode CSV rows into tuples. This is useful, but we sometimes
want a more specific type - a `Point` instead of an `(Int, Int)`, say. Case classes lend themselves well to such
scenarios, and kantan.csv has various mechanisms to support them.

Take, for example, the [wikipedia CSV example](https://en.wikipedia.org/wiki/Comma-separated_values#Example), which
we'll get from this project's resources:

```tut:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```tut
scala.io.Source.fromURL(rawData).mkString
```

An obvious representation of each row in this data would be:

```tut:silent
case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

We find ourselves with a particularly easy scenario to deal with: the rows in the CSV data and the fields in the target
case class have a 1-to-1 correspondence and are declared in the same order. This means that, if you don't mind a
[shapeless](shapeless.html) dependency, there's very little work to do:

```tut:silent
import kantan.csv.ops._     // kantan.csv syntax
import kantan.csv.generic._ // case class decoder derivation

val reader = rawData.asCsvReader[Car](',', true)
```

Let's make sure this worked by printing all decoded rows:

```tut
reader.foreach(println _)
```

As we said before though, this was a particularly advantageous scenario. How would we deal with a `Car` case class
where, say, the `year` and `make` fields have been swapped and the `desc` field doesn't exist?

```tut:silent
case class Car2(make: String, year: Int, model: String, price: Float)
```

This cannot be derived automatically, and we need to provide an instance of [`RowDecoder[Car2]`][`RowDecoder`]. This is
made easy by helper methods meant for just this problem, the various [`decoder`] methods:

```tut:silent
import kantan.csv._
implicit val carDecoder: RowDecoder[Car2] = RowDecoder.decoder(1, 0, 2, 4)(Car2.apply)
```

The first parameter to [`decoder`] is a list of indexes that map CSV columns to case class fields. The second one
is a function that takes 4 arguments and return a value of the type we want to create a decoder for - with a case class,
that's precisely the `apply` method declared in the companion object.

Let's verify that this worked as expected:

```tut
rawData.asCsvReader[Car2](',', true).foreach(println _)
```

## What to read next

If you want to learn more about:

* [decoding rows as arbitrary types](rows_as_arbitrary_types.html)
* [encoding case classes as rows](case_classes_as_rows.html)
* [how we were able to turn a `URL` into CSV data](csv_sources.html)


[`RowDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`decoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.RowDecoder$@decoder[A1,A2,A3,A4,R](i1:Int,i2:Int,i3:Int,i4:Int)(f:(A1,A2,A3,A4)=>R)(implicitevidence$7:kantan.csv.CellDecoder[A1],implicitevidence$8:kantan.csv.CellDecoder[A2],implicitevidence$9:kantan.csv.CellDecoder[A3],implicitevidence$10:kantan.csv.CellDecoder[A4]):kantan.csv.RowDecoder[R]
