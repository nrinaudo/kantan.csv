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
import tabulate._
import tabulate.ops._
```

`tabulate._` imports all the core classes, while `tabulate.ops._` brings the various operators in scope.


Additionally, most methods used to open CSV data for reading require an implicit `scala.io.Codec` to be in scope. I'll
be using `ISO-LATIN-1` here, but bear in mind no single charset will work for all CSV data.
Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```tut:silent
implicit val codec = scala.io.Codec.ISO8859
```

## Parsing basics
The simplest way of parsing CSV data is to call the `asCsvReader` method that enriches relevant types. We'll discuss 
what exactly "relevant types" mean a bit later, but for now, it's safe to consider that anything that can be turned into
a stream of characters (such as the `rawData` variable we declared earlier) is concerned.

The `asCsvReader` method takes two parameters: the separator character (usually `,`) and a boolean flag that indicates
whether or not to skip the first row.

More importantly, `asCsvReader` takes a type parameter that describes what each row should be interpreted as. We'll
study this mechanism in depth, but for now, an example: let's say that we want to represent each row in list of cars as
a list of strings.

```tut
rawData.asCsvReader[List[String]](',', false)
```

That return type is interesting. First, the outermost type: [`CsvReader`](/api/#tabulate.CsvReader). That's essentially
an iterator with a `close` method - you can fold on it, filter it, use it in monadic composition...

That `CsvReader` contains instances of [`DecodeResult`](/api/#tabulate.DecodeResult) - a sum type that
represents the result of decoding each row. It can correctly be thought of as a specialised version of `Option`.

Finally, the innermost type is what we requested each row to be parsed as: a `List[String]`.

And if we were to print each row, we'd see pretty much what we'd expect:

```tut
rawData.asCsvReader[List[String]](',', false).foreach(println _)
```

Note that you could have used any collection type instead of `List`, although not all would make sense. A `Set`, for
instance, would not be very useful, as the order of columns matter in our example.

## Parsing into useful types
This last example was interesting, but a bit underwhelming - rows as sequence of strings are a bit of a disappointment,
especially with a language like Scala where we like things typed to their eyeballs.

Luckily, tabulate supports parsing into most standard types (and has easy to use extension mechanisms for whatever is
not covered by default).

For example, we might want to parse our cars as tuples. Let's first declare a type alias for the sake of brevity:

```tut:silent
type CarTuple = (Int, String, String, Option[String], Float)
```

We can now call `asCsvReader` with a more meaningful type parameter:

```tut
rawData.asCsvReader[CarTuple](',', false).foreach(println _)
```

The thing that stands out is that the first row is a `DecodeFailure`: tabulate failed to parse it as an instance of 
`CarTuple`. That makes sense: our first row is a header and of different type than the others. We can just skip it by
passing `true` to `asCsvReader`:

```tut
rawData.asCsvReader[CarTuple](',', true).foreach(println _)
```

Notice how tabulate interpreted the empty descriptions as `None`. 

That's much better than our previous list of strings, but let's be honest: our cars are begging to be parsed into a
dedicated case class. This is where things can get slightly more complicated... 


## Parsing into case classes
### The simple case
If you don't mind a dependency on [shapeless](https://github.com/milessabin/shapeless) and if your CSV columns happen
to be in the exact same order as your case class fields, then things are still simple (if a bit CPU intensive during
compilation): you can call `asCsvReader` exactly as before, provided you import `tabulate.generic.codecs._`:

```tut
import tabulate.generic.codecs._

case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)

rawData.asCsvReader[Car](',', true).foreach(println _)
```

### The less simple case
It's also possible to define your decoding mechanism for case classes. The most common reason for that is your type
class fields and CSV columns are not defined in the same order, and you need to somehow specify what goes where.

This is much simpler than it sounds, however. First, let's redefine our `Car` case class to match this scenario:

```tut
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
```

Now, adding parsing support for a type is done by providing an implicit [`RowDecoder`](/api/#tabulate.RowDecoder)
instance for that type. There are lots of ways to achieve that result - write one from scratch, compose an existing
one... but case classes are so common that a helper function is provided: `RowDecoder.decoderAAA`, where `AAA` is the
arity of the case class for which to create a `RowDecoder`.

Our `Car` case class has 5 fields, which means we must use `RowDecoder.decoder5`. The first parameter is a function that
take a value for each field and returns an instance of the desired case class - a function that is always available as
the `apply` method of a case class' companion object. The other 5, curried parameters are the 0-based index in a row of
each field - `make` occurs in second position, for example, so its index is 1.
 
Let's write this:

```tut
implicit val carDecoder = RowDecoder.decoder5(Car.apply)(1, 2, 0, 4, 3)

rawData.asCsvReader[Car](',', true).foreach(println _)
```


## Supporting more types
Try as it might, tabulate cannot possibly support all possible variations on all possible types. It provides reasonable
defaults for standard types, but cannot support types it's not aware of or ones that don't have a reasonable default
format (dates come to mind).

We've already seen how to create new `RowDecoder` instances: this makes it simple to add support for, say, scalaz's
`Maybe` (although that's technically not strictly necessary as tabulate comes with a scalaz module *and* the `generic`
module is capable of deriving such instances automatically).

What we've not yet seen is add new _cell_ types - we've seen that `String` and `Int` were supported out of the box, for
instance, but what happens if we need to parse ISO formatted dates?

Perhaps unsurprisingly, this is very similar to row types: just provide an appropriate implicit instance of
[`CellDecoder`](/api/#tabulate.CellDecoder). For example:
 
```tut:silent
import java.text.SimpleDateFormat

implicit val dateDecoder = CellDecoder.fromUnsafe(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse)
```