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
libraryDependencies += "com.nrinaudo" %% "kantan.csv-joda-time" % "0.1.12"
```

You then need to import the corresponding package:

```scala
import kantan.csv.joda.time._
```

There are so many different ways of serialising dates that kantan.csv doesn't have a default implementation - whatever
the choice, it would end up more often wrong than right. What you can do, however, is declare an implicit
[`DateTimeFormat`]. This will get you a [`CellDecoder`] and [`CellEncoder`] instance for the following types:

* [`DateTime`]
* [`LocalDate`]
* [`LocalDateTime`]
* [`LocalTime`]

Let's imagine for example that we want to extract dates from the following string:

```scala
import kantan.csv.ops._

val input = "1,12-10-1978\n2,09-01-2015"
```

We'd first need to declare the appropriate [`DateTimeFormat`]:

```scala
import org.joda.time.format._

implicit val format = DateTimeFormat.forPattern("DD-MM-yyyy")
```

And we're done:

```scala
scala> val res = input.unsafeReadCsv[List, (Int, org.joda.time.LocalDate)](',', false)
res: List[(Int, org.joda.time.LocalDate)] = List((1,1978-01-12), (2,2015-01-09))
```

By the same token, we got an encoder for free:

```scala
scala> res.asCsv(',')
res2: String =
"1,12-01-1978
2,09-01-2015
"
```


[`Date`]:https://docs.oracle.com/javase/7/docs/api/java/util/Date.html
[`DateTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/DateTime.html
[`LocalDate`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalDate.html
[`LocalDateTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalDateTime.html
[`LocalTime`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/LocalTime.html
[`DateTimeFormat`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
[`CellEncoder`]:{{ site.baseUrl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`CellDecoder`]:{{ site.baseUrl }}/api/#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
