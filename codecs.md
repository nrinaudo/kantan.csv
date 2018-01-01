---
layout: tutorial
title: "Encoders, decoders and codecs"
section: tutorial
sort_order: 18
---
We've seen how kantan.csv uses encoders and decoders as a convenient way to support new types. This didn't account for
a fairly common scenario, however: types for which one wishes to declare both an encoder and a decoder. It's certainly
possible to write both, but it's a bit cumbersome. kantan.csv offers an alternative: codecs, which are simply an
encoder and a decoder rolled into one.

## Cell codecs

We've seen before how to create a [CellEncoder](arbitrary_types_as_cells.html) and a
[CellDecoder](cells_as_arbitrary_types.html) for joda's [`DateTime`]. [`CellCodec`] allows you to do the same thing, but
in one go:

```scala
import kantan.csv._
import kantan.csv.ops._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val jodaDateTime: CellCodec[DateTime] = {
  val format = ISODateTimeFormat.date()
  CellCodec.from(s ⇒ DecodeResult(format.parseDateTime(s)))(d ⇒ format.print(d))
}
```

And with that, we can now both encode and decode [`DateTime`]:

```scala
scala> val dates = List(
     |   List(new DateTime(), new DateTime().plusDays(1)),
     |   List(new DateTime().plusDays(2), new DateTime().plusDays(3))
     | ).asCsv(rfc)
dates: String =
"2018-01-01,2018-01-02
2018-01-03,2018-01-04
"

scala> dates.readCsv[List, List[DateTime]](rfc)
res1: List[kantan.csv.ReadResult[List[org.joda.time.DateTime]]] = List(Success(List(2018-01-01T00:00:00.000+01:00, 2018-01-02T00:00:00.000+01:00)), Success(List(2018-01-03T00:00:00.000+01:00, 2018-01-04T00:00:00.000+01:00)))
```


## Row codecs

There's a very similar mechanism for rows: [`RowCodec`]. This one is a bit more powerful and a bit more complicated,
however: all the helper methods we've seen for creating [`RowDecoder`] and [`RowEncoder`] instances also exist for
[`RowCodec`]. Let's take a concrete example with case classes.

```scala
final case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

We want to be able to both encode and decode that, so we can create a [`RowCodec[Person]`][`RowCodec`] instance:

```scala
implicit val personCodec: RowCodec[Person] = RowCodec.caseCodec(0, 2, 1)(Person.apply)(Person.unapply)
```

And with that one line, we're done:

```scala
scala> val csv = ps.asCsv(rfc)
csv: String =
"0,38,Nicolas
1,1,Kazuma
2,18,John
"

scala> csv.readCsv[List, Person](rfc)
res3: List[kantan.csv.ReadResult[Person]] = List(Success(Person(0,Nicolas,38)), Success(Person(1,Kazuma,1)), Success(Person(2,John,18)))
```

[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html
[`CellCodec`]:{{ site.baseurl }}/api/kantan/csv/package$$CellCodec.html
[`RowCodec`]:{{ site.baseurl }}/api/kantan/csv/package$$RowCodec.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
