---
layout: default
title:  "Writing CSV data"
section: tutorial
---

## Sample data
In this tutorial, we'll try to do the opposite as the [parsing one]({{ site.baseurl }}/tut/parsing.html): instead of
having CSV data to load in memory, we have the list of cars and need to write it out:

```scala
scala> case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
defined class Car

scala> val data = List(Car("Ford", "E350", 1997, 3000F, Some("ac, abs, moon")),
     |                 Car("Chevy", "Venture \"Extended Edition\"", 1999, 4900F, None),
     |                 Car("Chevy", "Venture \"Extended Edition, Very Large\"", 1999, 5000F, None),
     |                 Car("Jeep", "Grand Cherokee", 1996, 4799F, Some("MUST SELL!\nair, moon roof, loaded")))
data: List[Car] =
List(Car(Ford,E350,1997,3000.0,Some(ac, abs, moon)), Car(Chevy,Venture "Extended Edition",1999,4900.0,None), Car(Chevy,Venture "Extended Edition, Very Large",1999,5000.0,None), Car(Jeep,Grand Cherokee,1996,4799.0,Some(MUST SELL!
air, moon roof, loaded)))
```

## Setting up Tabulate
The code in this tutorial requires the following imports:

```scala
import tabulate._
import tabulate.ops._
```

`tabulate._` imports all the core classes, while `tabulate.ops._` bring the various operators in scope.


Additionally, most methods used to open CSV data for writing require an implicit `scala.io.Codec` to be in scope. I'll
be using `ISO-LATIN-1` here, but bear in mind no single charset will work for all CSV data.
Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```scala
implicit val codec = scala.io.Codec.ISO8859
```


## The `CsvWriter` class
All CSV serialisation is done through the [CsvWriter]({{ site.baseurl }}/api/#tabulate.CsvWriter) class,
instances of which can be retrieved through the `asCsvWriter` method that enriches types we can write to.

In order for the various examples' output to be more readable, I'll be using the following function to print
results out:

```scala
import java.io.StringWriter

def printCsv[A](as: List[A])(f: StringWriter => CsvWriter[A]): String = {
  val out = new StringWriter()
  
  // Go through each a in as, write it to f(out), then close it.
  as.foldLeft(f(out))(_ write _).close()
    
  out.toString
}
```

This gives you a fairly good idea of how `CsvWriter` works. You essentially need two methods:

* `write`, which takes an `A` for a `CsvWriter[A]` and writes it as a CSV row.
* `close`, which releases whatever resources were open once the whole data has been written.

Note that `CsvWriter` is meant to allow a fluent programming style: `write` returns the `CsvWriter` itself, allowing
you to chain calls (this is what the above example does in the fold, but it might not be obvious until pointed out).


## Writing collections of strings
The simplest way to serialise CSV data is as a list of collections of strings: each row is a list of cells, and each
cell is a string. You just need to know how to turn each element of your input data into a list of strings. For example:

```scala
def toStrings(c: Car): List[String] = List(c.year.toString, c.make, c.model, c.desc.getOrElse(""), c.price.toString)
```

You can than just use this function to map over your list of cars and serialize it:

```scala
scala> printCsv(data.map(toStrings))(_.asCsvWriter[List[String]](','))
res1: String =
"1997,Ford,E350,"ac, abs, moon",3000.0
1999,Chevy,"Venture ""Extended Edition""",,4900.0
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.0
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.0
"
```

The `asCsvWriter` takes one value argument, the character to use as a column separator, and a type one: the type of
of each row that will be passed to it.

Note that due to the way I've set the `printCsv` method up, the type parameter can actually be inferred by the compiler
and is not strictly necessary. I've left it there for clarity's sake, as it'll be required more often than not.


## Writing tuples
Writing data as sequences of strings is nice enough, but it forces you to transform each row yourself - not a big deal,
but not ideal either.

A better solution is to work with tuples and let `CsvWriter` work out how to turn each entry in the tuple to a string.

Let's turn our cars into tuples, declared as a type alias for the sake of brevity:

```scala
type CarTuple = (Int, String, String, Option[String], Float)

def toTuples(c: Car): CarTuple = (c.year, c.make, c.model, c.desc, c.price)
```

Serialising that list of tuples is done the way you'd expect, by specifying the right type to `asCsvWriter` and let it
work out the rest:

```scala
scala> printCsv(data.map(toTuples))(_.asCsvWriter[CarTuple](','))
res3: String =
"1997,Ford,E350,"ac, abs, moon",3000.0
1999,Chevy,"Venture ""Extended Edition""",,4900.0
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.0
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.0
"
```

As you can see, we didn't have to turn empty descriptions in `None`, nor call `toString` on non-string values. Just line
the types up correctly and everything works out.

We seem to have lost the ability to add a header row, however: it was easy before - a header row is a sequence of
strings, our data was sequences of strings, we could just stick it in there and the types would work out. Not anymore,
however - a header is most certainly not a value of type `CarTuple`.

The trick here is that the `asCsvWriter` method actually takes two parameters: the column separator *and* the header
row, which defaults to the empty sequence if not specified. If we'd wanted to add a header row, we'd have done it that
way:

```scala
scala> val header = List("Year", "Make", "Model", "Description", "Price")
header: List[String] = List(Year, Make, Model, Description, Price)

scala> printCsv(data.map(toTuples))(_.asCsvWriter[CarTuple](',', header))
res4: String =
"Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.0
1999,Chevy,"Venture ""Extended Edition""",,4900.0
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.0
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.0
"
```


## Writing case classes
While tuples are an obvious improvement to lists of strings, they still require us to transform each entry in our data.

A better, more idiomatic solution is to declare a `RowEncoder` that will do that for you. This is made particularly easy
here because our example is based on case classes, which have dedicated helper methods in `RowEncoder`:

```scala
scala> implicit val carEncoder= RowEncoder.caseEncoder5(Car.unapply)(1, 2, 0, 4, 3)
carEncoder: tabulate.RowEncoder[Car] = tabulate.RowEncoder$$anon$2@3e78c92d

scala> printCsv(data)(_.asCsvWriter[Car](',', header))
res5: String =
"Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.0
1999,Chevy,"Venture ""Extended Edition""",,4900.0
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.0
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.0
"
```

Note that the name `caseEncoder5` ends in a number: that's the number of fields in your case class.

The list of integers maps each field to its corresponding column in the CSV data. That is, the first int is the index of
the first field, the second one that of the second field...


## Advanced topics

### CSV data sinks
In this tutorial, we sort of took it for granted that `java.io.StringWriter` would have an `asCsvWriter` method. This
works thanks to the [CsvOutput]({{ site.baseurl }}/api/#tabulate.CsvOutput) type class: any type `A` that has an
implicit `CsvOutput[A]` in scope will be enriched with the `asCsvWriter` method.

As a simple example, this is how you'd add support for writing CSV to `java.io.File`:

```scala
import java.io._

implicit def fileOutput(implicit c: scala.io.Codec) =
  CsvOutput((f: File) => new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), c.charSet))) 
```

Note that files are already supported, as well as a few other types. An exhaustive list can be found in the
[CsvOutput]({{ site.baseurl }}/api/#tabulate.CsvOutput$)  companion object.


### CSV cell types
Another seemingly magical thing is how `CsvWriter` managed to guess how to turn each individual cell into a valid
string representation. This is achieved through the [CellEncoder]({{ site.baseurl }}/api/#tabulate.CellEncoder)
type class.

The following is how you'd add support for serialising instances of `java.util.Date` to their ISO 8601 representation:

```scala
import java.util.Date
import java.text.SimpleDateFormat

// Note that this is a def and not a val.
// SimpleDateFormat, in a burst of genius, carries state and is not thread safe.
def iso8601: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

implicit val dateEncoder = CellEncoder((d: Date) => iso8601.format(d))
```

We can now write:

```scala
scala> printCsv(List(Seq(new Date(), new Date(System.currentTimeMillis + 86400000))))(_.asCsvWriter[Seq[Date]](','))
res11: String =
"2015-11-01T12:57:31+0100,2015-11-02T12:57:31+0100
"
```

Note that should you need both serialise and de-serialise dates, you should use the `CellCodec` type class instead -
it's essentially a `CellEncoder` and a `CellDecoder` mixed into one:

```scala
implicit val dateCodec = CellCodec(s => DecodeResult(iso8601.parse(s)), (d: Date) => iso8601.format(d))
```


### CSV row types
Finally, `CsvWriter` relies on the [RowEncoder]({{ site.baseurl }}/api/#tabulate.RowEncoder) type class to turn
rows into actual CSV data.

The beauty of this pattern is that all these type classes seamlessly compose: `RowEncoder` relies on instances of
`CellEncoder`, which allows us to add global support to any type we desire.

For example, Tabulate does not support dates by default. We have, however, put a `CellDecoder[Date]` in scope in the
previous section, which allows us to write, say, `(Date, Date)` instances without any additional code:

```scala
scala> printCsv(List((new Date(), new Date(System.currentTimeMillis + 86400000))))(_.asCsvWriter[(Date, Date)](','))
res12: String =
"2015-11-01T12:57:31+0100,2015-11-02T12:57:31+0100
"
```

You can also add support for brand new row types that are not collections, tuples or case classes:

```scala
class Point2D(val x: Int, val y: Int)

implicit val p2dEncoder = RowEncoder((p2d: Point2D) => Seq(p2d.x.asCsvCell, p2d.y.asCsvCell))
```

This allows us to write the following:

```scala
scala> printCsv(List(new Point2D(1, 2), new Point2D(3, 4)))(_.asCsvWriter[Point2D](','))
res14: String =
"1,2
3,4
"
```

Note, however, that the various `RowEncoder.caseEncoderXXX` methods do not apply *only* to case classes. They can easily
be used for "normal" classes:

```scala
implicit val p2Encoder2 = RowEncoder.caseEncoder2((p: Point2D) => Some((p.x, p.y)))(0, 1)
```
