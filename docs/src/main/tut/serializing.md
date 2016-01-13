---
layout: default
title:  "Writing CSV data"
section: tutorial
---

## Setting up Tabulate
The code in this tutorial requires the following imports:

```tut:silent
import tabulate._     // Imports core classes.
import tabulate.ops._ // Enriches standard classes with CSV serialization methods.
```

Additionally, [Joda Time](http://www.joda.org/joda-time/) is expected to be in the `CLASSPATH` for a few examples, but
that's not a tabulate requirement.

## Serialization to `String`
The most basic, if not necessarily the most useful, way of serializing CSV data is to turn it all into a `String`. This
is achieved through the [`asCsv`] method that enriches all subclasses of [`Traversable`]:

```tut
List(List("ab", "cd"), List("ef", "gh")).asCsv(',')
```

[`asCsv`] takes two parameters: the column separator (more often than not `,`) and an optional header row.

Notice that in our example, each CSV row is a [`List[String]`][`List`], and that tabulate worked out automatically how
to write them. You should be able to use most standard types as well:

```tut
List(Set(1, 2), Set(3, 4)).asCsv(',')

List((Option(2.0F), true), (Option.empty[Float], false)).asCsv(',')
```

## Supporting non-standard types
While tabulate does a fairly good job at supporting most standard types out of the box, you might need to add support
for your own - because they are not part of the standard library, say, or don't have obvious default formats like ints
or booleans do.

### Cell types
At the lowest level, CSV data is composed of rows, themselves composed of cells. You might find yourself needing to
add support for new cell types - a common example is writing dates in a computer readable format.

Let's assume you want to serialize your dates as [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) (because why would
you want to use any other format?). In order to do so, you need to provide an implicit
[`CellEncoder[DateTime]`][`CellEncoder`], which is fairly simple:

```tut:silent
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

implicit val dateEncoder: CellEncoder[DateTime] = CellEncoder(d => ISODateTimeFormat.dateTime().print(d)) 
```

This allows you to serialize data that contains [`DateTime`] values without any further work:

```tut
List(("now", DateTime.now), ("yesterday", DateTime.now.minusDays(1))).asCsv(',')
```

Tabulate comes with a number of default implementations of [`CellEncoder`] which can all be found in its
[companion object]({{ site.baseurl }}/api/#tabulate.CellEncoder$).

### Row types
Row types work in a very similar fashion, but you need to provide an instance of [`RowEncoder`] instead.

Let's take the example of a custom case class, `Car` from our [parsing tutorial]({{ site.baseurl }}/tut/parsing.html):

```tut:silent
case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)

// Sample data, to be re-used in a few examples.
val cars = List(
  Car(1997, "Ford", "E350", Some("ac, abs, moon"), 3000F),
  Car(1999, "Chevy", "Venture \"Extended Edition\"", None, 4900F)
)
```

The most basic (and least convenient way) of adding serialization support for it would be to create a [`RowEncoder`]
manually:

```tut:silent
implicit val carEncoder: RowEncoder[Car] =
  RowEncoder(c => Seq(c.year.toString, c.make, c.model, c.desc.getOrElse(""), c.price.toString))
```

We'll be improving on that momentarily, but let's first make sure it works as expected:

```tut
cars.asCsv(',', Seq("Year", "Make", "Model", "Description", "Price"))
```

So yes, that works, but is frankly unpleasant to write *and* bypasses the [`CellEncoder`] mechanism entirely. A better
solution is to use the [`encoderAAA`][`encoder5`] methods, where `AAA` is the number of cells of each row. `Car` has 5
fields, so we need to use [`encoder5`]:

```tut:silent
implicit val carEncoder: RowEncoder[Car] = RowEncoder.encoder5(c => (c.year, c.make, c.model, c.desc, c.price))
```

This is already terser and more flexible, and behaves exactly the same. But we can improve on it further: if
you look at the function that is passed to [`encoder5`], it looks a *lot* like the `unapply` method found on the
companion object of all case classes. It looks so much like it that tabulate provides a helper function that uses that
instead:

```tut:silent
// The int list at the end maps each cell to a field in the case class.
implicit val carEncoder: RowEncoder[Car] = RowEncoder.caseEncoder5(Car.unapply)(0, 1, 2, 3, 4)
```

That's better still, but [`caseEncoderAAA`][`caseEncoder5`] must be used with caution: it unwraps the [`Option`]
returned by its function argument. That's safe in the case of a case class, but can fail for ad-hoc implementations. 

Note that our index arguments are sequential: we find ourselves in a special case, CSV cells and case class fields are
declared in exactly the same order. When this happens, and if you don't mind a [shapeless] dependency, we can improve
things further by omitting the encoder entirely. All we need to do is depend on tabulate's `generic` module and 
add the following import:

```tut:silent
import tabulate.generic.codecs._
```

Tabulate comes with a number of default implementations of [`RowEncoder`] which can all be found in its
[companion object]({{ site.baseurl }}/api/#tabulate.RowEncoder$).


## Serialization to "writable" types
[`asCsv`] is actually a special case of a more general function: types that "can be written to" are decorated with the
[`writeCsv`] method that takes a [`Traversable`] and serializes it as CSV.

First, let's explain what _type that can be written to_ means: any type for which there exists an implicit instance
of [`CsvOutput`] in scope. By default, that covers
[`File`](https://docs.oracle.com/javase/7/docs/api/java/io/File.html),
[`OutputStream`](https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html) and
[`Writer`](https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html).

Let's demonstrate this with a [`StringWriter`](https://docs.oracle.com/javase/7/docs/api/java/io/StringWriter.html):

```tut
new java.io.StringWriter().writeCsv(cars, ',', Seq("Year", "Make", "Model", "Description", "Price")).toString
```

## Step-by-step serialization
[`writeCsv`] itself is also a special case of a more general mechanism: under the hood, it retrieves an instance of
[`CsvWriter`] and works with that.

Retrieving a [`CsvWriter`] is achieved through the [`asCsvWriter`] method that enriches types that have an instance of
[`CsvOutput`] in scope. The previous example is thus equivalent to:

```tut
val out = new java.io.StringWriter()

cars.foldLeft(out.asCsvWriter(',', Seq("Year", "Make", "Model", "Description", "Price")))(_ write _).close

out.toString
```

This is often the preferred way of writing data to CSV, as it does not require having the whole data in memory at any
given time. A typical scenario would be to read tuples from a database and feed them, one by one, to a [`CsvWriter`]:
this will work regardless of the size of the database, as each tuple is serialized and forgotten before the next one is
loaded.


[`CsvWriter`]:{{ site.baseurl }}/api/#tabulate.CsvWriter
[`CellEncoder`]:{{ site.baseurl }}/api/#tabulate.CellEncoder
[`RowEncoder`]:{{ site.baseurl }}/api/#tabulate.RowEncoder
[`asCsvWriter`]:{{ site.baseurl }}/api/#tabulate.CsvOutput@writer[A](s:S,separator:Char,header:Seq[String])(implicitea:tabulate.RowEncoder[A],implicitengine:tabulate.engine.WriterEngine):tabulate.CsvWriter[A]
[`asCsv`]:{{ site.baseurl }}/api/#tabulate.ops$$TraversableOps@asCsv(sep:Char,header:Seq[String])(implicitengine:tabulate.engine.WriterEngine):String
[`CsvOutput`]:{{ site.baseurl }}/api/#tabulate.CsvOutput
[shapeless]:https://github.com/milessabin/shapeless
[`DateTime`]:http://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html
[`encoder5`]:{{ site.baseurl }}/api/#tabulate.RowEncoder$@encoder5[C,A0,A1,A2,A3,A4](f:C=>(A0,A1,A2,A3,A4))(implicita0:tabulate.CellEncoder[A0],implicita1:tabulate.CellEncoder[A1],implicita2:tabulate.CellEncoder[A2],implicita3:tabulate.CellEncoder[A3],implicita4:tabulate.CellEncoder[A4]):tabulate.RowEncoder[C]
[`caseEncoder5`]:{{ site.baseurl }}/api/#tabulate.RowEncoder$@caseEncoder5[C,A0,A1,A2,A3,A4](f:C=>Option[(A0,A1,A2,A3,A4)])(i0:Int,i1:Int,i2:Int,i3:Int,i4:Int)(implicita0:tabulate.CellEncoder[A0],implicita1:tabulate.CellEncoder[A1],implicita2:tabulate.CellEncoder[A2],implicita3:tabulate.CellEncoder[A3],implicita4:tabulate.CellEncoder[A4]):tabulate.RowEncoder[C]
[`Option`]:http://www.scala-lang.org/api/current/index.html#scala.Option
[`Traversable`]:http://www.scala-lang.org/api/current/index.html#scala.collection.Traversable
[`writeCsv`]:{{ site.baseurl }}/api/#tabulate.CsvOutput@write[A](out:S,rows:Traversable[A],sep:Char,header:Seq[String])(implicitevidence$1:tabulate.RowEncoder[A]):S
[`List`]:http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.List