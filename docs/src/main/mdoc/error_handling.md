---
layout: tutorial
title: "Error handling"
section: tutorial
sort_order: 9
---
There are many ways of dealing with parse errors in kantan.csv. This tutorial shows the most common strategies, but
it essentially boils down to knowing how [`Either`] (the underlying type of [`ReadResult`]) works.

All the examples here are going to be using the following data:

```
1,Nicolas,true
2,Kazuma,28
3,John,false
```

Note how the second row's third column is not of the same type as that of the other rows.

Let's first declare the basic things we need to decode such a CSV file (see [this](rows_as_case_classes) if it does
not make sense to you):

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, flag: Boolean)

val rawData: java.net.URL = getClass.getResource("/dodgy.csv")
```

## Throw on errors

The simplest, least desirable error handling mechanism is to ignore the possibility of failure and allow exceptions
to be thrown. This is achieved by using [`asUnsafeCsvReader`]:

```scala mdoc
scala.util.Try(rawData.asUnsafeCsvReader[Person](rfc).toList)
```

Note that this is hardly ever an acceptable solution. In idiomatic Scala, we pretend that exceptions don't exist and
rely on encoding errors in return types. Still, unsafe readers can be useful - when writing one-off scripts for which
reliability or maintainability are not an issue, for example.

## Drop errors
Another common, if not always viable strategy is to use [`collect`] to simply drop whatever rows failed to decode:

```scala mdoc
rawData.asCsvReader[Person](rfc).collect { case Right(a) => a }.toList
```

[`collect`] is a bit like a [`filter`] and a [`map`] rolled into one, and allows us to:

* ignore all failures
* extract the value of all successes

This is achieved in an entirely safe way, validated at compile time.


## Fail if at least one row fails to decode
When not streaming data, a good option is to fail if a single row fails to decode - turn a
[`List[ReadResult[A]]`][`List`] into a [`ReadResult[List[A]]`][`ReadResult`]. This is done through [`ReadResult`]'s
[`sequence`] method:

```scala mdoc
ReadResult.sequence(rawData.readCsv[List, Person](rfc))
```

The only real downside to this approach is that it requires loading the entire data in memory.

## Use more flexible types to prevent errors
Our problem here is that the `flag` field of our `Person` class is not always of the same type - some rows have it as a
`boolean`, others as an `Int`. This is something that the [`Either`] type is well suited for, so we could rewrite
`Person` as follows:

```scala mdoc:silent
case class SafePerson(id: Int, name: String, flag: Either[Boolean, Int])
```

We can now load the whole data without an error:

```scala mdoc
rawData.readCsv[List, SafePerson](rfc)
```

Following the same general idea, one could use [`Option`] for fields that are not always set.

This strategy is not always possible, but is good to keep in mind for these cases where it can be applied.


[`List`]:http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
[`asUnsafeCsvReader`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#asUnsafeCsvReader[B](sep:Char,header:Boolean)(implicitevidence$2:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[B]
[`ReadResult`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html
[`collect`]:http://nrinaudo.github.io/kantan.codecs/api/kantan/codecs/resource/ResourceIterator.html#collect[B](f:PartialFunction[A,B]):kantan.codecs.resource.ResourceIterator[B]
[`filter`]:http://nrinaudo.github.io/kantan.codecs/api/kantan/codecs/resource/ResourceIterator.html#filter(p:A=%3EBoolean):kantan.codecs.resource.ResourceIterator[A]
[`map`]:http://nrinaudo.github.io/kantan.codecs/api/kantan/codecs/resource/ResourceIterator.html#map[B](f:A=%3EB):kantan.codecs.resource.ResourceIterator[B]
[`Either`]:http://www.scala-lang.org/api/current/scala/util/Either.html
[`Option`]:http://www.scala-lang.org/api/current/scala/Option.html
[`sequence`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html#sequence[S,M[X]%3C:TraversableOnce[X]](rs:M[Either[F,S]])(implicitcbf:scala.collection.generic.CanBuildFrom[M[Either[F,S]],S,M[S]]):Either[F,M[S]]
[`ReadError`]:{{ site.baseurl }}/api/kantan/csv/ReadError.html
[`Right`]:http://www.scala-lang.org/api/current/scala/util/Right.html
