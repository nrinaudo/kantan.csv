---
layout: tutorial
title: "Encoding arbitrary types as rows"
section: tutorial
sort_order: 14
---
Other tutorials covered encoding [collections](collections_as_rows.html), [tuples](tuples_as_rows.html)
and [case classes](case_classes_as_rows.html) as CSV rows. While those are the most common scenarios, it is sometimes
necessary to encode types that are none of these.

Let's write such a type for the purpose of this tutorial:

```scala mdoc:silent
class Person(val id: Int, val name: String, val age: Int)

val ps = List(new Person(0, "Nicolas", 38), new Person(1, "Kazuma", 1), new Person(2, "John", 18))
```

We now have a [`List[Person]`][`List`] that we'd like to encode to CSV. This is done by providing an implicit instance
of [`RowEncoder[Person]`][`RowEncoder`] - you could write it from scratch, but it's usually simpler and more correct
to use one of the helper methods defined in the [companion object]({{ site.baseurl }}/api/kantan/csv/RowEncoder$.html).
In our case, we want [`encoder`]:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._

implicit val personEncoder: RowEncoder[Person] = RowEncoder.encoder(0, 2, 1)((p: Person) => (p.id, p.name, p.age))
```

kantan.csv will work out how to encode each individual field thanks to the [`CellEncoder`] mechanism describe in a
[previous post](arbitrary_types_as_cells.html).

Let's make sure this worked out as expected:

```scala mdoc
ps.asCsv(rfc)
```

## What to read next

If you want to learn more about:

* [encoding entire collections in one go](serializing_collections.html)
* [decoding rows as arbitrary types](rows_as_arbitrary_types.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)

[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`List`]:http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`encoder`]:{{ site.baseurl }}/api/kantan/csv/RowEncoder$.html#encoder[C,A1,A2,A3,A4](i1:Int,i2:Int,i3:Int,i4:Int)(f:C=>(A1,A2,A3,A4))(implicitevidence$7:kantan.csv.CellEncoder[A1],implicitevidence$8:kantan.csv.CellEncoder[A2],implicitevidence$9:kantan.csv.CellEncoder[A3],implicitevidence$10:kantan.csv.CellEncoder[A4]):kantan.csv.RowEncoder[C]
