---
layout: tutorial
title: "What can be parsed as CSV data?"
section: tutorial
sort_order: 8
---
Before we can even think about parsing CSV data, we need to have access to it somehow. kantan.csv extends most things
that "can be turned into CSV" with useful methods, such as the oft-used [`asCsvReader`] method. Among such things are:

* [`URL`]
* [`URI`]
* [`File`]
* [`String`]

This is done through the [`CsvSource`] type class: any type `A` such that there exists a value of type
[`CsvSource[A]`][`CsvSource`] in the implicit scope will be enriched with
[useful methods]({{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html).

Implementing your own instances of [`CsvSource`] for types that aren't supported by default is fairly simple.

## Implementation from scratch
Reduced to its simplest expression, a [`CsvSource`] is essentially a `A => ParseResult[Reader]` - that is, a function
that takes an `A` and turns it into a [`Reader`], with the possibility of safe failure encoded in [`ParseResult`].

If you can write such a function, you can trivially turn it into a valid instance of [`CsvSource`] - for example,
strings:

```scala mdoc:silent
import kantan.csv._
import java.io._

implicit val stringSource: CsvSource[String] = CsvSource.from(s => ParseResult(new StringReader(s)))
```


## Adapting existing instances
Most of the time though, it's easier to turn the type you wish to provide an instance for into a type that *already*
has an instance. This is achieved either through [`contramap`] (if the transformation is safe and cannot fail) or
[`econtramap`] (if, as with most IO-related things, it can fail). For example:

```scala mdoc:reset:silent
import kantan.csv._
import java.io._

implicit val stringInput: CsvSource[String] = CsvSource[Reader].contramap(s => new StringReader(s))
```


[`CsvSource`]:{{ site.baseurl }}/api/kantan/csv/CsvSource.html
[`ParseResult`]:{{ site.baseurl }}/api/kantan/csv/ParseResult$.html
[`asCsvReader`]:{{ site.baseurl }}/api/kantan/csv/ops/CsvSourceOps.html#asCsvReader[B](sep:Char,header:Boolean)(implicitevidence$1:kantan.csv.RowDecoder[B],implicitia:kantan.csv.CsvSource[A],implicite:kantan.csv.engine.ReaderEngine):kantan.csv.CsvReader[kantan.csv.ReadResult[B]]
[`URL`]:https://docs.oracle.com/javase/7/docs/api/java/net/URL.html
[`URI`]:https://docs.oracle.com/javase/7/docs/api/java/net/URI.html
[`File`]:https://docs.oracle.com/javase/7/docs/api/java/io/File.html
[`Reader`]:https://docs.oracle.com/javase/7/docs/api/java/io/Reader.html
[`contramap`]:{{ site.baseurl }}/api/kantan/csv/CsvSource.html#contramap[T](f:T=>S):kantan.csv.CsvSource[T]
[`econtramap`]:{{ site.baseurl }}/api/kantan/csv/CsvSource.html#econtramap[SS<:S,T](f:T=>kantan.csv.ParseResult[SS]):kantan.csv.CsvSource[T]
[`String`]:https://docs.oracle.com/javase/7/docs/api/java/lang/String.html
