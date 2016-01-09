---
layout: default
title:  "Parsing CSV data"
section: tutorial
---

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

* each row is composed of various types: `Int` for the year, for example, or `Option[String]` for the description.
* the first row is a header and not composed of the same types as the other ones.
* some of the more annoying corner cases of CSV are present: escaped double-quotes and multi-line rows.

I have this data as a resource, so let's just declare it:
 
```tut:silent
val rawData: java.net.URL = getClass.getResource("/wikipedia.csv")
```

## Setting up Tabulate
The code in this tutorial requires the following imports:

```tut:silent
import tabulate._     // Imports core classes.
import tabulate.ops._ // Enriches standard classes with CSV parsing methods.
```

Additionally, most methods used to open CSV data for reading require an implicit
[`Codec`](http://www.scala-lang.org/api/current/index.html#scala.io.Codec) to be in scope. I'll
be using `ISO-LATIN-1` here, but bear in mind no single charset will work for all CSV data.
Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```tut:silent
implicit val codec = scala.io.Codec.ISO8859
```

## Parsing basics
The simplest way of parsing CSV data is to call the [`asCsvReader`] method that enriches relevant types. We'll discuss 
what exactly "relevant types" mean a bit later, but for now, it's safe to consider that anything that can be turned into
a stream of characters (such as the `rawData` variable we declared earlier) is concerned.

The [`asCsvReader`] method takes two parameters: the separator character (usually `,`) and a boolean flag that indicates
whether or not to skip the first row.

More importantly, [`asCsvReader`] takes a type parameter that describes what each row should be interpreted as. We'll
study this mechanism in depth, but for now, an example: let's say that we want to represent each row in our list of cars
as a `List[String]`.

```tut
rawData.asCsvReader[List[String]](',', false)
```

That return type is interesting. First, the outermost type: [`CsvReader`]. That's essentially an iterator with a `close`
method - you can fold on it, filter it, use it in monadic composition...

That [`CsvReader`] contains instances of [`DecodeResult`] - a sum type that represents the result of decoding each row.
It can correctly be thought of as a specialised version of [`Option`].

Finally, the innermost type is what we requested each row to be parsed as: a `List[String]`.

And if we were to print each row, we'd see pretty much what we'd expect:

```tut
rawData.asCsvReader[List[String]](',', false).foreach(println _)
```

Note that you could have used any collection type instead of `List`, although not all would make sense. A `Set`, for
instance, would not be very useful, as the order of columns matters in our example.

## Parsing into useful types
That last example was interesting, but a bit underwhelming - rows as sequences of strings are a bit of a disappointment,
especially with a language like Scala where we like things typed to their eyeballs.

Luckily, tabulate supports parsing into most standard types (and has easy to use extension mechanisms for whatever is
not covered by default).

For example, we might want to parse our cars as tuples. Let's first declare a type alias for the sake of brevity:

```tut:silent
type CarTuple = (Int, String, String, Option[String], Float)
```

We can now call [`asCsvReader`] with a more meaningful type parameter:

```tut
rawData.asCsvReader[CarTuple](',', false).foreach(println _)
```

The thing that stands out is that the first row is a [`DecodeFailure`][DecodeFailure]: tabulate failed to parse it as an
instance of `CarTuple`. That makes sense: our first row is a header and composed of different types than the others. We
can just skip it by passing `true` to [`asCsvReader`]:

```tut
rawData.asCsvReader[CarTuple](',', true).foreach(println _)
```

Notice how tabulate interpreted the empty descriptions as `None`. 

That's much better than our previous list of strings, but let's be honest: our cars are begging to be parsed into a
dedicated case class. This is where things can get slightly more complicated... 


## Parsing into case classes
### The simple case
If you don't mind a dependency on [shapeless] and if your CSV columns happen to be in the exact same order as your case
class fields, then things are still simple (if a bit CPU intensive during compilation): you can call [`asCsvReader`]
exactly as before, provided you depend on the `generic` module and bring the codecs it declares in scope:

```tut
import tabulate.generic.codecs._

case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)

rawData.asCsvReader[Car](',', true).foreach(println _)
```


### The less simple case
It's also possible to define your own decoding mechanism for case classes. The most common reason for that is your case
class fields and CSV columns are not defined in the same order, and you need to somehow specify what goes where.

This is much simpler than it sounds, however. First, let's redefine our `Car` case class by shuffling its fields to
match this scenario:

```tut:silent
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
```

Now, adding parsing support for a row type is done by providing an implicit [`RowDecoder`]
instance for that type. There are lots of ways to achieve that result - write one from scratch, compose on an existing
one... but case classes are so common that a helper function is provided: `RowDecoder.decoderAAA`, where `AAA` is the
arity of the case class for which to create a [`RowDecoder`].

Our `Car` case class has 5 fields, which means we must use `RowDecoder.decoder5`. The first parameter is a function that
take a value for each field and returns an instance of the desired case class - a function that is always available as
the `apply` method of a case class' companion object. The other 5, curried parameters are the 0-based index in a row of
each field - `make` occurs in second position, for example, so its index is 1.
 
Let's write this:

```tut:silent
implicit val carDecoder = RowDecoder.decoder5(Car.apply)(1, 2, 0, 4, 3)
```

And that's it. We're now capable of parsing into instances of `Car`, even though the CSV data and case class fields are
not in the same order:

```tut
rawData.asCsvReader[Car](',', true).foreach(println _)
```

Tabulate comes with a number of default implementations of [`RowDecoder`] which can all be found in its
[companion object](/api/#tabulate.RowDecoder$).

## Parsing non-standard types
So far, we've seen how tabulate can treat rows as collections of values and assemble them into useful types. Our use
cases have been simple, however: each row has always been composed of standard Scala types, which are supported out of
the box.

What would happen if we'd decided to represent a car's `year` field as a date? Well, let's check. First, let's rewrite
`Car` to use [`DateTime`] for its `year` field:

```tut:silent
import org.joda.time.DateTime

case class Car(year: DateTime, make: String, model: String, desc: Option[String], price: Float)
```

What happens when we try to parse into this new `Car` type?

```tut:fail
rawData.asCsvReader[Car](',', true).foreach(println _)
```

That was to be expected. [`DateTime`] is not part of the standard Scala library, and tabulate cannot provide
the required glue for it.

Doing so is rather straightforward, however: all you need to do is provide an implicit [`CellDecoder`] for
[`DateTime`]. We already have such an instance for `Int`, so we can just summon that and turn it into what
we want:

```tut:silent
implicit val yearDecoder: CellDecoder[DateTime] = CellDecoder[Int].map(year => new DateTime(year, 1, 1, 0, 0))
```

If this looks a bit like magic to you, here are the keys you need to work it out:

* `CellDecoder[Int]` is syntactic sugar for `CellDecoder.apply[Int]`, which returns an implicit instance for its type
  parameter if it can find one and fails the compilation if it can't.
* The `map` method of [`CellDecoder`] takes an `A => B` and turns a `CellDecoder[A]` into a
  `CellDecoder[B]`.
  
Armed with that new decoder, we can now parse our CSV data the way we'd expect:

```tut
rawData.asCsvReader[Car](',', true).foreach(println _)
```

Tabulate comes with a number of default implementations of [`CellDecoder`] which can all be found in its
[companion object](/api/#tabulate.CellDecoder$).


## Convenience methods
### Unsafe parsing
By default, tabulate parses things safely: if an error occurs, no exception is thrown and you don't lose control of
your code. Instead, the possibility for errors is encoded in [`DecodeResult`] and you have the
opportunity, for example, to skip over incorrectly encoded rows.
 
Sometimes though, you really want your code to crash if something goes wrong - you're writing a one-off bit of code
whose sole purpose is to turn CSV into better shaped data, say. You can use [`asUnsafeCsvReader`] for
this purpose: it'll unwrap the [`DecodeResult`] layer and throw an exception at the first error.

```tut
rawData.asUnsafeCsvReader[Car](',', true).foreach(println _)
```

Note that I strongly advise against using unsafe parsing in any code that has the slightest chance of being used near
production systems, it's a recipe for disaster. But should you need unsafe parsing, it's there.


### Parsing everything in one go
Tabulate works by letting you iterate over your CSV data. That's the safest way of doing things: since you never load
more than one row at any given time, you don't run the risk of running out of memory when working with abnormally large
files.

Sometimes however, you know your data is small enough to fit in memory and you'd really like to have it as a `List`,
say, or whatever other collection strikes your fancy.

The [`readCsv`] method serves just that purpose. It works exactly the same way as [`asCsvReader`],
but with an additional type parameter: that of the collection in which to store the result.

```tut
rawData.readCsv[List, Car](',', true)
```

[`readCsv`] also has an unsafe variant, [`unsafeReadCsv`] (used here with a `Set` to show it can
be done):

```tut
rawData.unsafeReadCsv[Set, Car](',', true)
```


## What can be parsed as CSV?
So far, we've been using `rawData` as our source of CSV and sort of accepting that it was enriched with all these
methods. But, just like with [`CellDecoder`] and [`RowDecoder`], the underlying mechanism
is both fairly simple and easily extendable. 

The type class you're looking for is [`CsvInput`]. Looking at its methods, you'll see that all one needs to
implement to turn a type `A` into a source of CSV data is a function that turns an `A` into a
[Reader](https://docs.oracle.com/javase/7/docs/api/java/io/Reader.html).

Most of the time, you shouldn't need to declare new instances of [`CsvInput`]. Should the need arise, however,
the idiomatic way of doing so is to use one of the existing implementations and call
[contramap](/api/#tabulate.CsvInput@contramap[T](f:T=>S):tabulate.CsvInput[T]). Say that you want to write a
[`CsvInput`] for strings, for instance (a purely academic endeavour, as one is provided by default):

```tut:silent
implicit val strInput: CsvInput[String] = CsvInput[java.io.Reader].contramap(s => new java.io.StringReader(s))
```

Tabulate comes with a number of default implementations of [`CsvInput`] which can all be found in its
[companion object](/api/#tabulate.CsvInput$).

[`Option`]:http://www.scala-lang.org/api/current/index.html#scala.Option
[`CsvReader`]:/api/#tabulate.CsvReader
[`CsvInput`]:/api/#tabulate.CsvInput
[`RowDecoder`]:/api/#tabulate.RowDecoder
[`CellDecoder`]:/api/#tabulate.CellDecoder
[`DecodeResult`]:/api/#tabulate.DecodeResult
[`DecodeFailure`]:/api/#tabulate.DecodeResult$$DecodeFailure$
[`readCsv`]:/api/index.html#tabulate.CsvInput@read[C[_],A](S,Char,Boolean)(RowDecoder[A],ReaderEngine,CanBuildFrom[Nothing,DecodeResult[A],C[DecodeResult[A]]]):C[DecodeResult[A]]
[`unsafeReadCsv`]:/api/index.html#tabulate.CsvInput@unsafeRead[C[_],A](S,Char,Boolean)(RowDecoder[A],ReaderEngine,CanBuildFrom[Nothing,DecodeResult[A],C[DecodeResult[A]]]):C[DecodeResult[A]]
[`asCsvReader`]:/api/index.html#tabulate.CsvInput@reader[A](s:S,separator:Char,header:Boolean)(implicitevidence$1:tabulate.RowDecoder[A],implicitengine:tabulate.engine.ReaderEngine):tabulate.CsvReader[tabulate.DecodeResult[A]]
[`asUnsafeCsvReader`]:/api/index.html#tabulate.CsvInput@unsafeReader[A](s:S,separator:Char,header:Boolean)(implicitevidence$1:tabulate.RowDecoder[A],implicitengine:tabulate.engine.ReaderEngine):tabulate.CsvReader[tabulate.DecodeResult[A]]
[shapeless]:https://github.com/milessabin/shapeless
[`DateTime`]:http://www.joda.org/joda-time/apidocs/index.html