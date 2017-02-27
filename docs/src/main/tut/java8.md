---
layout: tutorial
title: "Java 8 dates and times"
section: tutorial
sort_order: 26
---
Java 8 comes with a better thought out dates and times API. Unfortunately, it cannot be supported as part of the core
kantan.csv API - we still support Java 7. There is, however, a dedicated optional module that you can include by
adding the following line to your `build.sbt` file:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8" % "0.1.18"
```

You then need to import the corresponding package:

```tut:silent
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

```tut:silent
import java.time._
import kantan.csv._
import kantan.csv.ops._

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
import java.time.format.DateTimeFormatter

val input = "1,10-12-1978\n2,09-01-2015"

val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
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

[`GroupDecoder`]:{{ site.baseurl }}/api/kantan/regex/package$$GroupDecoder.html
[`Instant`]:https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
[`LocalDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html
[`OffsetDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html
[`ZonedDateTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html
[`LocalDate`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html
[`LocalTime`]:https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html
