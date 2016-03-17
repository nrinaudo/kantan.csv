---
layout: default
title:  "Decoding rows as case classes"
section: tutorial
---

## Overview
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

This kind of data is often represented as case classes:

```tut:silent
case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)
```

We find ourselves with a particularly easy scenario to deal with: the rows in the CSV data and the fields in the target
case class have a 1-to-1 correspondence and are declared in the same order. This means that, if you don't mind a
[shapeless](16-shapeless.html) dependency, there's very little work to do:

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
made easy by helper methods meant for just this problem, the various
[`RowDecoder.decoderXXX`][`decoder4`] methods, where `XXX` is the
number of values we're interested in extracting. In our case, `Car2` has four fields, so we want [`decoder4`]:

```tut:silent
import kantan.csv._
implicit val carDecoder: RowDecoder[Car2] = RowDecoder.decoder4(Car2.apply)(1, 0, 2, 4)
```

The first parameter to [`decoder4`] is a function that takes 4 arguments and return a value of the type we want to
create a decoder for - with a case class, that's precisely the `apply` method declared in the companion object.

We must then provide a list of integer values representing the index in a CSV row at which to look for the data of
the corresponding field.

Let's verify that this worked as expected:

```tut
rawData.asCsvReader[Car2](',', true).foreach(println _)
```


## What to read next

If you want to learn more about:

* [decoders and codecs](14-codecs.html)
* [how we were able to turn a `URI` into CSV data](07-csv_sources.html)
 

[`RowDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`decoder4`]:{{ site.baseurl }}/api/#kantan.csv.RowDecoder$@decoder4[A1,A2,A3,A4,R](f:(A1,A2,A3,A4)=>R)(i1:Int,i2:Int,i3:Int,i4:Int)(implicita1:kantan.csv.CellDecoder[A1],implicita2:kantan.csv.CellDecoder[A2],implicita3:kantan.csv.CellDecoder[A3],implicita4:kantan.csv.CellDecoder[A4]):kantan.csv.RowDecoder[R]