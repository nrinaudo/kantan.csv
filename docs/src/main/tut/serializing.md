---
layout: default
title:  "Writing CSV data"
section: tutorial
---

```tut:invisible
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
implicit val codec = scala.io.Codec.ISO8859
```

## Sample data
In this part of the tutorial, we'll try to do the opposite as the previous one: instead of having CSV data to load
in memory, we have the list of cars loaded in memory and need to write it out:

```tut
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])

val data = List(Car("Ford", "E350", 1997, 3000F, Some("ac, abs, moon")),
                Car("Chevy", "Venture \"Extended Edition\"", 1999, 4900F, None),
                Car("Chevy", "Venture \"Extended Edition, Very Large\"", 1999, 5000F, None),
                Car("Jeep", "Grand Cherokee", 1996, 4799F, Some("MUST SELL!\nair, moon roof, loaded")))
```

## The `CsvWriter` class
All CSV serialisation is done through the `CsvWriter` class, instances of which you can retrieve through the
`asCsvWriter` method that enriches types we can write to.

In our examples, we'll write to a `java.io.StringWriter` - this'll allow us to see what was actually written. Other
types are supported - `java.io.File`, for example, or `java.io.OutputStream`.


## Writing collections of strings
The simplest way to serialise CSV data is as a list of collections of strings: each row is a list of cells, and each
cell is a string. You just need to know how to turn each element of your input data into a list of strings. For example:

```tut
val strOut = new java.io.StringWriter()

val ss = data.map(c => List(c.year.toString, c.make, c.model, c.desc.getOrElse(""), c.price.toString)) 

ss.foldLeft(strOut.asCsvWriter[Seq[String]](','))(_ write _).close

strOut.toString
```

The `asCsvWriter` takes one value argument, the character to use as a column separator, and a type one: the type of
of each row that will be passed to it.


## Writing tuples
Writing data as sequences of strings is nice enough, but it forces you to transform each row yourself - not a big deal,
but not ideal either.

A better solution is to work with tuples and let `CsvWriter` work out how to turn each entry in the tuple to a string.

Let's turn our cars into tuples, declared as a type alias for the sake of brevity:

```tut
type CarTuple = (Int, String, String, Option[String], Float)

val ts: List[CarTuple] = data.map(c => (c.year, c.make, c.model, c.desc, c.price))
```

Serialising that list of tuples is done the way you'd expect, by specifying the right type to `asCsvWriter` and let it
work out the rest:

```tut
val tupleOut = new java.io.StringWriter

ts.foldLeft(tupleOut.asCsvWriter[CarTuple](','))(_ write _).close

tupleOut.toString
```

As you can see, we didn't have to turn empty descriptions in `None`, nor call `toString` on non-string values. Just line
the types up correctly and the rest just works out.

We seem to have lost the ability to add a header row, however: it was easy before - a header row is a sequence of
strings, our data was sequences of strings, we could just stick it in there and the types would work out. Not anymore,
however - a header is most certainly not a value of type `CarTuple`.

The trick here is that the `asCsvWriter` method actually takes two parameters: the column separator *and* the header
row, which defaults to the empty sequence if not specified. If we'd wanted to add a header row, we'd have done it that
way:

```tut
val headerOut = new java.io.StringWriter
val header = List("Year", "Make", "Model", "Description", "Price")

ts.foldLeft(headerOut.asCsvWriter[CarTuple](',', header))(_ write _).close

headerOut.toString
```


## Writing case classes
While tuples are an obvious improvement to lists of strings, they still require us to transform each entry in our data.

A better, more idiomatic solution is to declare a `RowEncoder` that will do that for you. This is made particularly easy
here because our example is based on case classes, which have dedicated helper methods in `RowEncoder`:

```tut
implicit val carEncoder= RowEncoder.caseEncoder5(Car.unapply)(1, 2, 0, 4, 3)

val caseOut = new java.io.StringWriter

data.foldLeft(caseOut.asCsvWriter[Car](','))(_ write _).close

caseOut.toString
```

Note that `caseEncoder5` ends in a number: that's the number of fields in your case class. The list of integers maps
each field to its corresponding column in the CSV data. That is, the first int is the index of the first field, the
second one that of the second field...


## Advanced topics

### CSV data sinks
In this tutorial, we sort of took it for granted that `java.io.StringWriter` would have an `asCsvWriter` method. This
works thanks to the `CsvOutput` type class: any type `A` that has an implicit `CsvOutput[A]` in scope will be
enriched with the `asCsvWriter` method.

As a simple example, this is how you'd add support for writing CSV to `java.io.File`:

```tut
import java.io._

implicit def fileOutput(implicit c: scala.io.Codec) =
  CsvOutput((f: File) => new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), c.charSet))) 
```

Note that files are already supported, as well as a few other types. An exhaustive list can be found in the
`CsvOutput` companion object.


### CSV cell types
Another seemingly magical thing is how `CsvWriter` managed to guess how to turn each individual cell into a valid
string representation. This is achieved through the `CellEncoder` type class.

The following is how you'd add support for serialising instances of `java.util.Date` to their ISO 8601 representation:
```tut
implicit val dateEncoder =
  CellEncoder((d: java.util.Date) => new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d))
```

Note that should you need both serialise and de-serialise dates, you should use the `CellCodec` type class instead -
it's essentially a `CellEncoder` and a `CellDecoder` mixed into one.


### CSV row types
Finally, `CsvWriter` relies on the `RowEncoder` type class to turn rows into actual CSV data.
