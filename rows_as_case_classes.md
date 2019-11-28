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

```scala
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```scala
scala.io.Source.fromURL(rawData).mkString
// res0: String = """Year,Make,Model,Description,Price
// 1997,Ford,E350,"ac, abs, moon",3000.00
// 1999,Chevy,"Venture ""Extended Edition""","",4900.00
// 1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
// 1996,Jeep,Grand Cherokee,"MUST SELL!
// air, moon roof, loaded",4799.00"""
```

An obvious representation of each row in this data would be:

```scala
case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

We find ourselves with a particularly easy scenario to deal with: the rows in the CSV data and the fields in the target
case class have a 1-to-1 correspondence and are declared in the same order. This means that, if you don't mind a
[shapeless](shapeless.html) dependency, there's very little work to do.

You'll first need to add a dependency to the [generic](shapeless.html) module in your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.6.0"
```

Then, with the appropriate imports:

```scala
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

val reader = rawData.asCsvReader[Car](rfc.withHeader)
```

Let's make sure this worked by printing all decoded rows:

```scala
reader.foreach(println _)
// Right(Car(1997,Ford,E350,Some(ac, abs, moon),3000.0))
// Right(Car(1999,Chevy,Venture "Extended Edition",None,4900.0))
// Right(Car(1999,Chevy,Venture "Extended Edition, Very Large",None,5000.0))
// Right(Car(1996,Jeep,Grand Cherokee,Some(MUST SELL!
// air, moon roof, loaded),4799.0))
```

As we said before though, this was a particularly advantageous scenario. How would we deal with a `Car` case class
where, say, the `year` and `make` fields have been swapped and the `desc` field doesn't exist?

```scala
case class Car2(make: String, year: Int, model: String, price: Float)
```

This cannot be derived automatically, and we need to provide an instance of [`RowDecoder[Car2]`][`RowDecoder`]. This is
made easy by helper methods meant for just this problem, the various [`decoder`] methods:

```scala
import kantan.csv._
implicit val car2Decoder: RowDecoder[Car2] = RowDecoder.decoder(1, 0, 2, 4)(Car2.apply)
```

The first parameter to [`decoder`] is a list of indexes that map CSV columns to case class fields. The second one
is a function that takes 4 arguments and return a value of the type we want to create a decoder for - with a case class,
that's precisely the `apply` method declared in the companion object.

Let's verify that this worked as expected:

```scala
rawData.asCsvReader[Car2](rfc.withHeader).foreach(println _)
// Right(Car2(Ford,1997,E350,3000.0))
// Right(Car2(Chevy,1999,Venture "Extended Edition",4900.0))
// Right(Car2(Chevy,1999,Venture "Extended Edition, Very Large",5000.0))
// Right(Car2(Jeep,1996,Grand Cherokee,4799.0))
```

## What to read next

If you want to learn more about:

* [decoding rows as arbitrary types](rows_as_arbitrary_types.html)
* [encoding case classes as rows](case_classes_as_rows.html)
* [how we were able to turn a `URL` into CSV data](csv_sources.html)


[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
[`decoder`]:{{ site.baseurl }}/api/kantan/csv/RowDecoder$.html#decoder[A1,A2,A3,A4,R](i1:Int,i2:Int,i3:Int,i4:Int)(f:(A1,A2,A3,A4)=>R)(implicitevidence$7:kantan.csv.CellDecoder[A1],implicitevidence$8:kantan.csv.CellDecoder[A2],implicitevidence$9:kantan.csv.CellDecoder[A3],implicitevidence$10:kantan.csv.CellDecoder[A4]):kantan.csv.RowDecoder[R]
