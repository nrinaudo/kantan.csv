---
layout: tutorial
title: "Joda-time module"
section: tutorial
sort_order: 26
---
[Joda-Time](http://www.joda.org/joda-time/) is a very well thought out date and time library for Java that happens to
be very popular in Scala - at the very least, it's quite a bit better than the stdlib [`Date`]. kantan.csv provides
support for it through a dedicated module.

The `joda-time` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-joda-time" % "0.2.0"
```

You then need to import the corresponding package:

```tut:silent
import kantan.csv.joda.time._
```

kantan.csv has default, ISO 8601 compliant [`CellDecoder`] and [`CellEncoder`] instances for the following types:

* [`DateTime`]
* [`LocalDate`]
* [`LocalDateTime`]
* [`LocalTime`]

Let's imagine for example that we want to extract dates from the following string:

```tut:silent
import kantan.csv._
import kantan.csv.ops._
import org.joda.time._

val input = "1,1978-12-10\n2,2015-01-09"
```

This is directly supported:

```tut
val res = input.unsafeReadCsv[List, (Int, LocalDate)](rfc)

res.asCsv(rfc)
```

It's also possible to declare your own [`CellDecoder`] and [`CellEncoder`] instances. Let's take, for example,
the following custom format:

```tut:silent
import org.joda.time.format._

val input = "1,10-12-1978\n2,09-01-2015"

val format = DateTimeFormat.forPattern("dd-MM-yyyy")
```

We then need to build a decoder for it and stick it in the implicit scope:

```tut:silent
implicit val decoder: CellDecoder[LocalDate] = localDateDecoder(format)
```

And we're done:

```tut
val res = input.unsafeReadCsv[List, (Int, LocalDate)](rfc)
```

Similarly, this is how you create and encoder:

```tut:silent
implicit val encoder: CellEncoder[LocalDate] = localDateEncoder(format)
```

And you can now easily encode data that contains instances of [`LocalDate`]:

```tut
res.asCsv(rfc)
```

Note that if you're going to both encode and decode dates, you can create a [`CellCodec`] in a single call instead:

```tut:silent
implicit val codec: CellCodec[LocalDate] = localDateCodec(format)
```




[`Date`]:https://docs.oracle.com/javase/7/docs/api/java/util/Date.html
[`DateTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/DateTime.html
[`LocalDate`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalDate.html
[`LocalDateTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalDateTime.html
[`LocalTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalTime.html
[`DateTimeFormat`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`CellCodec`]:{{ site.baseurl }}/api/kantan/csv/package$$CellCodec.html
