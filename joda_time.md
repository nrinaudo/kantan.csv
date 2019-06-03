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
libraryDependencies += "com.nrinaudo" %% "kantan.csv-joda-time" % "0.5.1"
```

You then need to import the corresponding package:

```scala
import kantan.csv.joda.time._
```

kantan.csv has default, ISO 8601 compliant [`CellDecoder`] and [`CellEncoder`] instances for the following types:

* [`DateTime`]
* [`LocalDate`]
* [`LocalDateTime`]
* [`LocalTime`]

Let's imagine for example that we want to extract dates from the following string:

```scala
import kantan.csv._
import kantan.csv.ops._
import org.joda.time._

val input = "1,1978-12-10\n2,2015-01-09"
```

This is directly supported:

```scala
scala> val res = input.unsafeReadCsv[List, (Int, LocalDate)](rfc)
res: List[(Int, org.joda.time.LocalDate)] = List((1,1978-12-10), (2,2015-01-09))

scala> res.asCsv(rfc)
res0: String =
"1,1978-12-10
2,2015-01-09
"
```

It's also possible to declare your own [`CellDecoder`] and [`CellEncoder`] instances. Let's take, for example,
the following custom format:

```scala
import org.joda.time.format._

val input = "1,10-12-1978\n2,09-01-2015"

val format = DateTimeFormat.forPattern("dd-MM-yyyy")
```

We then need to build a decoder for it and stick it in the implicit scope:

```scala
implicit val decoder: CellDecoder[LocalDate] = localDateDecoder(format)
```

And we're done:

```scala
scala> val res = input.unsafeReadCsv[List, (Int, LocalDate)](rfc)
res: List[(Int, org.joda.time.LocalDate)] = List((1,1978-12-10), (2,2015-01-09))
```

Similarly, this is how you create and encoder:

```scala
implicit val encoder: CellEncoder[LocalDate] = localDateEncoder(format)
```

And you can now easily encode data that contains instances of [`LocalDate`]:

```scala
scala> res.asCsv(rfc)
res1: String =
"1,10-12-1978
2,09-01-2015
"
```

Note that if you're going to both encode and decode dates, you can create a [`CellCodec`] in a single call instead:

```scala
implicit val codec: CellCodec[LocalDate] = localDateCodec(format)
```

Note that while you can pass a [`DateTimeFormatter`] directly, the preferred way of dealing with pattern strings is to
use the literal syntax provided by kantan.csv:

```scala
localDateDecoder(fmt"dd-MM-yyyy")
```

The advantage is that this is checked at compile time - invalid pattern strings will cause a compilation error:

```scala
scala> localDateDecoder(fmt"FOOBAR")
<console>:31: error: Invalid pattern: 'FOOBAR'
       localDateDecoder(fmt"FOOBAR")
                            ^
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
[`DateTimeFormatter`]:https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
