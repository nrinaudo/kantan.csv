---
layout: tutorial
title: "Joda-time module"
section: tutorial
sort_order: 25
---
[Joda-Time](http://www.joda.org/joda-time/) is a very well thought out date and time library for Java that happens to
be very popular in Scala - at the very least, it's quite a bit better than the stdlib [`Date`]. kantan.csv provides
support for it through a dedicated module.

The `joda-time` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-joda-time" % "0.1.15"
```

You then need to import the corresponding package:

```tut:silent
import kantan.csv.joda.time._
```

There are so many different ways of serialising dates that kantan.csv doesn't have a default implementation - whatever
the choice, it would end up more often wrong than right.

If you can provide a [`DateTimeFormat`] instance, however, you can easily get [`CellDecoder`], [`CellEncoder`] and
[`CellCodec`] instances for the following types:

* [`DateTime`]
* [`LocalDate`]
* [`LocalDateTime`]
* [`LocalTime`]

Let's imagine for example that we want to extract dates from the following string:

```tut:silent
import kantan.csv.ops._

val input = "1,12-10-1978\n2,09-01-2015"
```

We'd first need to declare the appropriate [`DateTimeFormat`]:

```tut:silent
import org.joda.time.format._

val format = DateTimeFormat.forPattern("DD-MM-yyyy")
```

We then need to build a decoder for it and stick it in the implicit scope:

```tut:silent
import kantan.csv.joda.time._

implicit val decoder = localDateDecoder(format)
```

And we're done:

```tut
val res = input.unsafeReadCsv[List, (Int, org.joda.time.LocalDate)](',', false)
```

Similarly, this is how you create and encoder:

```tut:silent
implicit val encoder = localDateEncoder(format)
```

And you can now easily encode data that contains instances of [`LocalDate`]:

```tut
res.asCsv(',')
```

Note that if you're going to both encode and decode dates, you can create a [`CellCodec`] in a single call instead:

```tut:silent
implicit val encoder = localDateCodec(format)
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
