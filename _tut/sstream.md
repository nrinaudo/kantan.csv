---
layout: default
title:  "Integration with scalaz-stream"
section: tutorial
---
This tutorial assumes you've already read up on how to [parse]({{ site.baseurl }}/tut/parsing.html) and
[serialise]({{ site.baseurl }}/tut/serializing.html) CSV data.

Tabulate's integration with scalaz-stream is rather simple: it "only" provides way to treat CSV data as either sources
or sinks. As such, rather than going through a list of all the possible actions, we're going to tackle a simple task:
take our usual cars data, filter out any entry whose description is not set, and write the remaining cars out as CSV.

## Sample data
The data is the same as usual:

```
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

This is available as a resource, so let's just a get the URL to that:
 
```scala
val rawData = getClass.getResource("/wikipedia.csv")
```

## Setting up Tabulate
The scalaz-stream integration requires the following imports:

```scala
import com.nrinaudo.csv._
import com.nrinaudo.csv.scalaz.stream._
import com.nrinaudo.csv.scalaz.stream.ops._
```

This tutorial also uses the scalaz `Maybe` type, which is supported through the following imports:

```scala
import _root_.scalaz.Maybe
import com.nrinaudo.csv.scalaz._
```

We also need to declare what charset the CSV should be read / written as:
 
```scala
implicit val codec = scala.io.Codec.ISO8859
```

## The `Car` case class
All the types you can parse CSV data as described in the [parsing tutorial]({{ site.baseurl }}/tut/parsing.html) still
apply. We'll just use a case class here, but you could use collections of strings if the fancy strikes you.

First, let's declare our case class:

```scala
case class Car(make: String, model: String, year: Int, price: Float, desc: Maybe[String])
```

We're going to need to both parse and serialise instances of that case class, so let's get the codec out of the way:

```scala
implicit val carCodec = RowCodec.caseCodec5(Car.apply, Car.unapply)(1, 2, 0, 4, 3)
```


## Creating a complete process
Since `java.net.URL` has a valid instance of `CsvInput`, we can turn `rawData` into a `Process[Task, Car]` with the
following code:

```scala
scala> rawData.asUnsafeCsvSource[Car](',', true)
res0: scalaz.stream.Process[scalaz.concurrent.Task,Car] = Append(Await(scalaz.concurrent.Task@7c0f4446,<function1>,<function1>),Vector(<function1>))
```

We set the arbitrary goal of filtering out all cars that have an empty description, which is done trivially:

```scala
scala> rawData.asUnsafeCsvSource[Car](',', true).
     |   filter(_.desc.isJust)
res1: scalaz.stream.Process[scalaz.concurrent.Task,Car] = Append(Halt(End),Vector(<function1>))
```

Finally, we want to write the remaining cars as CSV. For our example, we'll write it to a `StringWriter`, which will
allow us to inspect the result.

Since `StringWriter` has a valid instance of `CsvOutput`, we can turn any instance into a `Sink[Task, Car]`:

```scala
val out = new java.io.StringWriter()

val process = rawData.asUnsafeCsvSource[Car](',', true).
  filter(_.desc.isJust).
  to(out.asCsvSink(',', Seq("Year", "Make", "Model", "Description", "Price")))
```

We can now run the process and inspect the content of `out`:

```scala
scala> process.run.run

scala> out.toString
res4: String =
"Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.0
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.0
"
```
