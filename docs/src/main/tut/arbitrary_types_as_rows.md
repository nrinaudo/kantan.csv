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

```tut:silent
class Person(val id: Int, val name: String, val age: Int)

val ps = List(new Person(0, "Nicolas", 38), new Person(1, "Kazuma", 1), new Person(2, "John", 18))
```

We now have a [`List[Person]`][`List`] that we'd like to encode to CSV. This is done by providing an implicit instance
of [`RowEncoder[Person]`][`RowEncoder`] - you could write it from scratch, but it's usually simpler and more correct
to use one of the helper methods defined in the [companion object]({{ site.baseurl }}/api/#kantan.csv.RowEncoder$).
In our case, we want [`encoder`]:

```tut:silent
import kantan.csv._
import kantan.csv.ops._

implicit val personEncoder = RowEncoder.encoder(0, 2, 1)((p: Person) ⇒ (p.id, p.name, p.age))
```

kantan.csv will work out how to encode each individual field thanks to the [`CellEncoder`] mechanism describe in a
[previous post](arbitrary_types_as_cells.html).

Let's make sure this worked out as expected:

```tut
ps.asCsv(',')
```

## What to read next

If you want to learn more about:

* [encoding entire collections in one go](serializing_collections.html)
* [decoding rows as arbitrary types](rows_as_arbitrary_types.html)
* [how we were able to turn a `File` into a `CsvWriter`](csv_sinks.html)

[`CellEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`List`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`encoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.RowEncoder$@encoder[C,A1,A2,A3](i1:Int,i2:Int,i3:Int)(f:C=>(A1,A2,A3))(implicite1:kantan.csv.CellEncoder[A1],implicite2:kantan.csv.CellEncoder[A2],implicite3:kantan.csv.CellEncoder[A3]):kantan.csv.RowEncoder[C]
