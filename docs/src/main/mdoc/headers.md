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

```scala mdoc:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

This is what this data looks like:

```scala mdoc
scala.io.Source.fromURL(rawData).mkString
```

An obvious representation of each row in this data would be:

```scala mdoc:silent
case class Car(year: Int, make: String, model: String, price: Float, desc: Option[String])
```

Note how we've made sure to swap `desc` and `price`, to make sure that they were not in the same order as in the CSV
file.

Let's also add the usual kantan.csv imports:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
```

## Creating a decoder

At this point, we'd usually declare a [`RowDecoder`] instance, but we've decided to work with headers rather than
indexes, so we need a [`HeaderDecoder`].

Here's how we declare one:

```scala mdoc:silent
implicit val carDecoder: HeaderDecoder[Car] = HeaderDecoder.decoder("Year", "Make", "Model", "Price", "Description")(Car.apply _)
```

Note how this takes two parameter lists:

* the first one contains one `String` per attribute in `Car`: the corresponding header label.
* the second one is a function that turns the corresponding values into an instance of `Car`.

Through the magic of type classes, kantan.csv works out what types are expected and decodes them, and we get the
desired result:

```scala mdoc
rawData.asCsvReader[Car](rfc.withHeader).foreach(println _)
```

## Creating an encoder

In a similar fashion, you can create a [`HeaderEncoder`] to have kantan.csv automatically add the correct header when
serializing:

```scala mdoc:silent
implicit val carEncoder: HeaderEncoder[Car] = HeaderEncoder.caseEncoder("Year", "Make", "Model", "Price", "Description")(Car.unapply _)
```

This lets you write:

```scala mdoc
List(Car(1999, "Ford", "E350", 3000F, Some("ac, abs, moon"))).asCsv(rfc.withHeader)
```

## Creating a codec

In case you need both a [`HeaderDecoder`] and a [`HeaderEncoder`], you can also create a [`HeaderCodec`]:

```scala mdoc:silent
val carCodec: HeaderCodec[Car] = HeaderCodec.caseCodec("Year", "Make", "Model", "Price", "Description")(Car.apply _)(Car.unapply _)
```

[`HeaderDecoder`]:{{ site.baseurl }}/api/kantan/csv/HeaderDecoder.html
[`HeaderEncoder`]:{{ site.baseurl }}/api/kantan/csv/HeaderEncoder.html
[`HeaderCodec`]:{{ site.baseurl }}/api/kantan/csv/HeaderCodec.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
