---
layout: tutorial
title: "Using header values instead of indexes"
section: tutorial
sort_order: 19
---
kantan.csv mostly works from column indexes: when you need to refer to a specific CSV cell, you do so through its
index in a row. The reason behind this behaviour is that headers are entirely optional and cannot be relied on to always
be present.

Under some circumstances though, it would be helpful to be able to refer to CSV cells by their corresponding header
label - it's a fairly common scenario when interfacing with people that work with python, for example, where the norm
is to work with headers and disregard column order.

kantan.csv supports this, in a limited fashion, through [`HeaderDecoder`] and [`HeaderEncoder`].

Let's take the [wikipedia CSV example](https://en.wikipedia.org/wiki/Comma-separated_values#Example), which
we'll get from this project's resources:

```scala
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```scala
scala> scala.io.Source.fromURL(rawData).mkString
res0: String =
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

An obvious representation of each row in this data would be:

```scala
final case class Car(year: Int, make: String, model: String, price: Float, desc: Option[String])
```

Note how we've made sure to swap `desc` and `price`, to make sure that they were not in the same order as in the CSV
file.

Let's also add the usual kantan.csv imports:

```scala
import kantan.csv._
import kantan.csv.ops._
```

## Creating a decoder

At this point, we'd usually declare a [`RowDecoder`] instance, but we've decided to work with headers rather than
indexes, so we need a [`HeaderDecoder`].

Here's how we declare one:

```scala
implicit val carDecoder: HeaderDecoder[Car] = HeaderDecoder.decoder("Year", "Make", "Model", "Price", "Description")(Car.apply _)
```

Note how this takes two parameter lists:

* the first one contains one `String` per attribute in `Car`: the corresponding header label.
* the second one is a function that turns the corresponding values into an instance of `Car`.

Through the magic of type classes, kantan.csv works out what types are expected and decodes them, and we get the
desired result:

```scala
scala> rawData.asCsvReader[Car](rfc.withHeader).foreach(println _)
Right(Car(1997,Ford,E350,3000.0,Some(ac, abs, moon)))
Right(Car(1999,Chevy,Venture "Extended Edition",4900.0,None))
Right(Car(1999,Chevy,Venture "Extended Edition, Very Large",5000.0,None))
Right(Car(1996,Jeep,Grand Cherokee,4799.0,Some(MUST SELL!
air, moon roof, loaded)))
```

## Creating an encoder

In a similar fashion, you can create a [`HeaderEncoder`] to have kantan.csv automatically add the correct header when
serialising:

```scala
implicit val carEncoder: HeaderEncoder[Car] = HeaderEncoder.caseEncoder("Year", "Make", "Model", "Price", "Description")(Car.unapply _)
```

This lets you write:

```scala
scala> List(Car(1999, "Ford", "E350", 3000F, Some("ac, abs, moon"))).asCsv(rfc.withHeader)
res2: String =
"Year,Make,Model,Price,Description
1999,Ford,E350,3000.0,"ac, abs, moon"
"
```

## Creating a codec

In case you need both a [`HeaderDecoder`] and a [`HeaderEncoder`], you can also create a [`HeaderCodec`]:

```scala
implicit val carCodec: HeaderCodec[Car] = HeaderCodec.caseCodec("Year", "Make", "Model", "Price", "Description")(Car.apply _)(Car.unapply _)
```

[`HeaderDecoder`]:{{ site.baseurl }}/api/kantan/csv/HeaderDecoder.html
[`HeaderEncoder`]:{{ site.baseurl }}/api/kantan/csv/HeaderEncoder.html
[`HeaderCodec`]:{{ site.baseurl }}/api/kantan/csv/HeaderCodec.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
