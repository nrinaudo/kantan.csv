---
layout: tutorial
title: "Default instances"
section: tutorial
sort: 19
---
## Cells

### Basic types
The following types have [`CellCodec`] instances available out of the box:

* [`BigDecimal`]
* [`BigInt`]
* [`Byte`]
* [`Char`]
* [`Boolean`]
* [`Double`]
* [`Float`]
* [`Int`]
* [`Long`]
* [`Short`]
* [`String`]
* [`UUID`]
* [`URI`]
* [`URL`]

### `java.util.Date`

There also is a default [`CellCodec`] instance available for [`Date`], but this one is slightly more complicated. There
are so many different ways of writing dates that there is no reasonable default behaviour - one might argue that
defaulting to ISO 8601 might make sense, but there doesn't appear to be a sane way of implementing that in Java's
crusty date / time API.

Instead of providing a default implementation that is likely going to be incorrect for most people, kantan.csv
expects an implicit [`DateFormat`] instance in scope, and will decode and encode using that format.

Note that kantan.csv has a joda-time module, a very well thought out alternative to [`Date`].

### `Either`

For any two types `A` and `B` that each have a [`CellEncoder`], there exists a
[`CellEncoder[Either[A, B]]`][`CellEncoder`].

By the same token, for any two types `A` and `B` that each have a [`CellDecoder`], there exists a
[`CellDecoder[Either[A, B]]`][`CellDecoder`].

This is useful for dodgy CSV data where the type of a column is not well defined - it might sometimes be an int, 
sometimes a boolean, for example.

### `Option`

For any type `A` that as a [`CellEncoder`], there exists a [`CellEncoder[Option[A]]`][`CellEncoder`].
 
By the same token, for any type `A` that as a [`CellDecoder`], there exists a [`CellDecoder[Option[A]]`][`CellDecoder`].

This is useful for CSV data where some fields are optional and an empty value is legal.

### Composition

It's important to realise that these instances compose automatically. `Int` has a [`CellDecoder`], which means that
[`Option[Int]`][`Option`] does to. `Boolean` has a [`CellDecoder`], which means that
[`Either[Option[Int], Boolean]`][`Either`] does
too.


## Rows

### Single columns

For any type `A` that has a [`CellEncoder`], there exists a [`RowEncoder[A]`][`RowEncoder`].

By the same token, for any type `A` that has a [`CellDecoder`], there exists a [`RowDecoder[A]`][`RowDecoder`].

This is useful for CSV data where each row is composed of a single column.


### Tuples

Tuples of any arity, provided they're composed of types that all have a [`CellEncoder`], have a [`RowEncoder`].

By the same token, tuples of any arity, provided they're composed of types that all have a [`CellDecoder`], have a
[`RowDecoder`].

For example, `(Int, String, Either[Boolean, Option[Double]])` has both a [`RowEncoder`] and a [`RowDecoder`].


### Collections

For any first order type `F` that has a [`CanBuildFrom`] and type `A` that has a [`CellDecoder`], there exists a
[`RowDecoder[F[A]]`][`RowDecoder`].

For any type F that extends [`TraversableOnce`] and `A` that has a [`CellEncoder`], there exists a
[`RowEncoder[F[A]]`][`RowEncoder`].

This is useful for CSV data where each row is composed of homogeneous data.

### Either

For any two types `A` and `B` that each have a [`RowEncoder`], there exists a
[`RowEncoder[Either[A, B]]`][`RowEncoder`].

By the same token, for any two types `A` and `B` that each have a [`RowDecoder`], there exists a
[`RowDecoder[Either[A, B]]`][`RowDecoder`].

This is useful for dodgy CSV data where the type of a row is not well defined - it might sometimes be a `Cat`, sometimes
a `Dog`, for example.

### Option

For any type `A` that as a [`RowEncoder`], there exists a [`RowEncoder[Option[A]]`][`RowEncoder`].
 
By the same token, for any type `A` that as a [`RowDecoder`], there exists a [`RowDecoder[Option[A]]`][`RowDecoder`].

This is useful for CSV data where some rows might be empty. Note that this is a bit of a dodgy case: the RFC states
that empty rows should be ignored, so serialising a collection that contains `None` values and then deserialising the
result will not yield the original list.


## `CsvInput`

The following types have an instance of [`CsvInput`] out of the box:

* [`Reader`]
* [`InputStream`]
* [`File`]
* `Array[Byte]`
* `Array[Char`]
* [`Path`]
* `String`
* [`URL`]
* [`URI`]

## `CsvOutput`

The following types have an instance of [`CsvOutput`] out of the box:

* [`Writer`]
* [`OutputStream`]
* [`Path`]
* [`File`]

[`CellCodec`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellCodec[A]=kantan.codecs.Codec[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellDecoder[A]=kantan.codecs.Decoder[String,A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`CellEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@CellEncoder[A]=kantan.codecs.Encoder[String,A,kantan.csv.codecs.type]
[`RowDecoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowDecoder[A]=kantan.codecs.Decoder[Seq[String],A,kantan.csv.DecodeError,kantan.csv.codecs.type]
[`RowEncoder`]:{{ site.baseurl }}/api/index.html#kantan.csv.package@RowEncoder[A]=kantan.codecs.Encoder[Seq[String],A,kantan.csv.codecs.type]
[`Reader`]:https://docs.oracle.com/javase/7/docs/api/java/io/Reader.html
[`File`]:https://docs.oracle.com/javase/7/docs/api/java/io/File.html
[`InputStream`]:https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
[`Writer`]:https://docs.oracle.com/javase/7/docs/api/java/io/Writer.html
[`OutputStream`]:https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html
[`java.util.Date`]:https://docs.oracle.com/javase/7/docs/api/java/util/Date.html
[`CsvOutput`]:{{ site.baseurl }}/api/#kantan.csv.CsvOutput
[`DateFormat`]:https://docs.oracle.com/javase/7/docs/api/java/text/DateFormat.html
[`CsvInput`]:{{ site.baseurl }}/api/#kantan.csv.CsvInput
[`Option`]:http://www.scala-lang.org/api/current/index.html#scala.Option
[`Either`]:http://www.scala-lang.org/api/current/index.html#scala.util.Either
[`Path`]:https://docs.oracle.com/javase/7/docs/api/java/nio/file/Path.html
[`BigInt`]:http://www.scala-lang.org/api/current/index.html#scala.math.BigInt
[`BigDecimal`]:http://www.scala-lang.org/api/current/index.html#scala.math.BigDecimal
[`Byte`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html
[`Char`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Character.html
[`Boolean`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Boolean.html
[`Double`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Double.html
[`Float`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Float.html
[`Int`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html
[`Long`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Long.html
[`Short`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Short.html
[`String`]:https://docs.oracle.com/javase/7/docs/api/java/lang/String.html
[`UUID`]:https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html
[`URL`]:https://docs.oracle.com/javase/7/docs/api/java/net/URL.html
[`URI`]:https://docs.oracle.com/javase/7/docs/api/java/net/URI.html
[`CanBuildFrom`]:http://www.scala-lang.org/api/current/index.html#scala.collection.generic.CanBuildFrom
[`TraversableOnce`]:http://www.scala-lang.org/api/current/index.html#scala.collection.TraversableOnce
[`Date`]:https://docs.oracle.com/javase/7/docs/api/java/util/Date.html
