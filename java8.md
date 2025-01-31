---
layout: tutorial
title: "Java 8 dates and times"
section: tutorial
sort_order: 27
---
Java 8 comes with a better thought out dates and times API. Unfortunately, it cannot be supported as part of the core
kantan.csv API - we still support Java 7. There is, however, a dedicated optional module that you can include by
adding the following line to your `build.sbt` file:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8" % "0.8.0"
```

You then need to import the corresponding package:

```scala
import kantan.csv.java8._
```

kantan.csv has default, ISO 8601 compliant [`CellDecoder`] and [`CellEncoder`] instances for the following types:

* [`Instant`]
* [`LocalDateTime`]
* [`ZonedDateTime`]
* [`OffsetDateTime`]
* [`LocalDate`]
* [`LocalTime`]

Let's imagine for example that we want to extract dates from the following string:

```scala
import java.time._
import kantan.csv._
import kantan.csv.ops._

val plainInput = "1,1978-12-10\n2,2015-01-09"
```

This is directly supported:

```scala
val res = plainInput.unsafeReadCsv[List, (Int, LocalDate)](rfc)
// res: List[(Int, LocalDate)] = List((1, 1978-12-10), (2, 2015-01-09))

res.asCsv(rfc)
// res0: String = """1,1978-12-10
// 2,2015-01-09
// """
```

It's also possible to declare your own [`CellDecoder`] and [`CellEncoder`] instances. Let's take, for example,
the following custom format:

```scala
import java.time.format.DateTimeFormatter
import java.time._
import kantan.csv._
import kantan.csv.java8._
import kantan.csv.ops._

val input = "1,10-12-1978\n2,09-01-2015"

val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
```

We then need to build a decoder for it and stick it in the implicit scope:

```scala
implicit val decoder: CellDecoder[LocalDate] = localDateDecoder(format)
```

And we're done:

```scala
val result = input.unsafeReadCsv[List, (Int, LocalDate)](rfc)
// result: List[(Int, LocalDate)] = List((1, 1978-12-10), (2, 2015-01-09))
```

Similarly, this is how you create and encoder:

```scala
implicit val encoder: CellEncoder[LocalDate] = localDateEncoder(format)
```

And you can now easily encode data that contains instances of [`LocalDate`]:

```scala
result.asCsv(rfc)
// res2: String = """1,10-12-1978
// 2,09-01-2015
// """
```

Note that if you're going to both encode and decode dates, you can create a [`CellCodec`] in a single call instead:

```scala
val codec: CellCodec[LocalDate] = localDateCodec(format)
```

Note that while you can pass a [`DateTimeFormatter`] directly, the preferred way of dealing with pattern strings is to
use the literal syntax provided by kantan.csv:

```scala
localDateDecoder(fmt"dd-MM-yyyy")
```

The advantage is that this is checked at compile time - invalid pattern strings will cause a compilation error:

```scala
localDateDecoder(fmt"FOOBAR")
// error: Illegal format: 'FOOBAR'
```

[`GroupDecoder`]:{{ site.baseurl }}/api/kantan/regex/package$$GroupDecoder.html
[`Instant`]:https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
[`LocalDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html
[`OffsetDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html
[`ZonedDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html
[`LocalDate`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html
[`LocalTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html
[`DateTimeFormatter`]:http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormatter.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellDecoder.html
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`CellCodec`]:{{ site.baseurl }}/api/kantan/csv/package$$CellCodec.html
