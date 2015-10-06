---
layout: default
title:  "Parsing CSV data"
section: tutorial
---


```tut:invisible
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
implicit val codec = scala.io.Codec.ISO8859
```

## Sample data
Over the course of this tutorial, we'll be trying to parse the
[wikipedia CSV example](https://en.wikipedia.org/wiki/Comma-separated_values#Example):

```
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

A few things to note about this data:

* the first row is a header and not composed of the same types as the other ones.
* each row is composed of various types: `Int` for the year, for example, or `Option[String]` for the description.
* some of the more annoying corner cases of CSV are present: escaped double-quotes and multi-line rows.

I have this data as a resource, so let's just declare it:
 
```tut
val rawData = getClass.getResource("/wikipedia.csv")
```

## Rows as collections of strings
The simplest way to represent a CSV row is as a collection of strings. You do that through the `asCsvRows` method
that enriches any type that can be used as a source of CSV data:

```tut
rawData.asCsvRows[List[String]](',', false).toList

rawData.asCsvRows[Set[String]](',', false).toList
```

The `asCsvRows` method expects two parameters:

* the character to use as column separator. `,` is fairly common, but you'll find a lot of `;` or more esoteric
  separators in the wild.
* whether or not to skip the first row. Not terribly useful yet, but it'll become more important when we attempt to
  parse each row into more interesting types than just strings.
  
The type parameter describes how each CSV row should be represented. In the previous example, we've used `List[String]`
and `Set[String]`, but you should be able to use any standard collection class (technically, any collection that has
a `CanBuildFrom` instance, but I think that's every one of them).

Other sections of this tutorial give examples of how to use more complex types, but more often than not, you'll find
that you can just stick whatever assemblage of standard types you want in there and have it work out of the box. If
your data is composed of rows of floats for example, you can just request `List[Float]`, or even "complex" types
like `List[Either[Boolean, Option[Float]]]`.

Finally, pay attention to the return types in the previous example: where we asked for lists of strings, we actually
got each row as an `DecodeResult[List[String]]`: rows that could not be parsed will be represented as failures rather
than throw an exception that forcefully interrupts parsing. One possible use case is to filter out anything that isn't
properly formatted:

```tut
rawData.asCsvRows[List[String]](',', false).filter(_.isSuccess).toList
```

Alternatively, you can use `asUnsafeCsvRows` rather than `asCsvRows`. This will "flatten" the results, removing the
`DecodeResult` layer but throwing an exception if any problem is encountered.

```tut
rawData.asUnsafeCsvRows[List[String]](',', false).toList
```


## Rows as tuples
Collections of strings are a nice start, but not entirely satisfactory: our example is composed of values that should
be represented with more precise types. One simple way of doing that is asking to parse each row as a tuple (declared
here as a type alias for the sake of legibility):

```tut
type CarTuple = (Int, String, String, Option[String], Float)

rawData.asCsvRows[CarTuple](',', false).toList
```

Note, however, that the first row comes back as `DecodeFailure`. Remember that our data contains a header, composed
of types that do not actually map to those we specified to `asCSvRows`.

Of course, you could just filter out any row that isn't properly parsed, but that's not quite right: there's a huge
difference between a header and a row that was expected to parse but didn't. The better solution is simply to
pass `true` to the second parameter of `asCsvRows`, asking it to skip the header:

```tut
rawData.asCsvRows[CarTuple](',', true).toList
```

The difference between the two is more obvious when using `asUnsafeCsvRows`.

The following fails, since the first row is not a legal tuple. Note that we're trying to skip the first row, but it's
too late: the iterator's `drop` method is called *after* the corresponding row is parsed.

```tut
try {
  rawData.asUnsafeCsvRows[CarTuple](',', false).drop(1).toList
} catch { case e: Exception => e.getMessage }
```

This, however, does not fail: the header is skipped and the rest is valid.

```tut
rawData.asUnsafeCsvRows[CarTuple](',', true).toList
```


## Rows as case classes
Tuples are a definite improvement over collections of strings. More often than not, however, you'll find yourself
working with more specific types, usually case classes that the rest of your code knows how to manipulate.

Let's define such a case class for our example:

```tut
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
```

The process is slightly more involved than what we've seen so far, as decoders for case classes cannot be automatically
inferred in a type-safe way (that I could find). 

You can however trivially create one using one of the `RowDecoder.caseDecoderXXX`, where XXX is the number of fields
in your case class:

```tut
implicit val carDecoder = RowDecoder.caseDecoder5(Car.apply)(1, 2, 0, 4, 3)
rawData.asUnsafeCsvRows[Car](',', true).toList
```

Note the second parameter list: each int value corresponds to the index in a CSV row of the field at the corresponding
position. That is, the first value is the index of the first field, the second value that of the second field...

It's also worth noting that if you're also going to serialise your type to CSV, you're probably better off using
`RowCodec` instead:

```tut
implicit val carCodec = RowCodec.caseCodec5(Car.apply, Car.unapply)(1, 2, 0, 4, 3)
rawData.asUnsafeCsvRows[Car](',', true).toList
```

At this point, you can easily turn CSV data into an iterator over business specific types. This is where you can start
actually doing interesting things with your data, such as finding the car that has a description and the highest price:

```tut
rawData.asUnsafeCsvRows[Car](',', true).filter(_.desc.isDefined).maxBy(_.price)
```

## Advanced topics

### CSV data sources
One of the things this tutorial sort of glossed over is how our `rawData` variable was enriched with the
`asCsvRows` method.

Under the hood, this relies on the [CsvInput]({{ site.baseurl }}/api/#com.nrinaudo.csv.CsvInput) type class.
You don't really need to know what a type class is in order to use them: if you need to turn something into a source of
CSV data, just write a `CsvInput` instance for it, make it implicit, stick it in scope and you're done.

As a simple example, this is how you'd turn all strings into sources of CSV data:

```tut
implicit val stringInput = CsvInput((s: String) => scala.io.Source.fromString(s))
"a,b,c\nd,e,f".asCsvRows[Seq[Char]](',', false).toList
```

Note that there actually already is such an instance available for strings, as well as for many other types (
`java.io.File`, `java.net.URI`, `scala.io.Source`...). You can find an exhaustive list in the 
[CsvInput]({{ site.baseurl }}/api/#com.nrinaudo.csv.CsvInput$) companion object.


### CSV cell types
Another thing that was given the hand-wavy treatment is how each cell is parsed. When requesting each row as a list
of ints, for example, how do we know how to parse ints?

This is also done through a type class (as is just about everything here, really):
[CellDecoder]({{ site.baseurl }}/api/#com.nrinaudo.csv.CellDecoder). If you need to add support for new types, 
declare an implicit instance of `CellDecoder` for it. For example, if your CSV data contains ISO 8601 dates:

```tut
implicit val dateDecoder =
CellDecoder(s => DecodeResult(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s)))

"2012-01-01T12:00:00+0100,2013-01-01T12:00:00+0100,2014-01-01T12:00:00+0100".
  asCsvRows[Seq[java.util.Date]](',', false).toList
```

A lot of standard types are supported out of the box, including "complex" ones such as `Either` or `Option`:

```tut
"a,2,c".asCsvRows[List[Either[Int,Char]]](',', false).toList

"a,,c".asCsvRows[List[Option[Char]]](',', false).toList
```

You can find the complete list in the [CellDecoder]({{ site.baseurl }}/api/#com.nrinaudo.csv.CellDecoder$) companion object.


### CSV row types
You might already have guessed that the magic of guessing how to parse entire CSV rows simply by knowing what types
they're expected to contain is also type class based. In this case, the type class you're looking for is
[RowDecoder]({{ site.baseurl }}/api/#com.nrinaudo.csv.RowDecoder).

The beauty of the pattern is that `RowDecoder` relies on `CellDecoder` for parsing individual cells. For example,
dates are not supported (because there are so many formats they can be serialized as), but we've just added a
`CellDecoder` instance for them, which allows us to write:

```tut
"2012-01-01T12:00:00+0100,a".asCsvRows[(java.util.Date, Char)](',', false).toList
```

The `RowDecoder` type class allows you to add parsing support for types that are not collections, tuples or case
classes:

```tut
class Point2D(val x: Int, val y: Int) {
  override def toString = s"($x,$y)"
}

implicit val p2dDecoder = RowDecoder { ss =>
  for {
    x <- CellDecoder[Int].decode(ss, 0)
    y <- CellDecoder[Int].decode(ss, 1)
  } yield new Point2D(x, y)
}

"1,2\n3,4".asCsvRows[Point2D](',', false).toList
```