---
layout: tutorial
title: "Cats module"
section: tutorial
sort_order: 21
---
Kantan.csv has a [cats](https://github.com/typelevel/cats) module that is, in its current incarnation, fairly bare
bones: it simply provides a few useful type class instances.

The `cats` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "0.1.15"
```

You then need to import the corresponding package:

```tut:silent
import kantan.csv.cats._
```

## Cats instances

The following instance for cats type classes are provided:

* [`Functor`] for all decoders ([`CellDecoder`] and [`RowDecoder`]).
* [`Contravariant`] for all encoders ([`CellEncoder`] and [`RowEncoder`]).
* [`Order`] for all result types ([`ReadResult`], [`ParseResult`] and [`DecodeResult`]).
* [`Show`] for all result types.
* [`Monoid`] for all result types.
* [`Traverse`] for all result types.
* [`Monad`] for all result types.
* [`BiFunctor`] for all result types.

[`Functor`]:http://typelevel.org/cats/api/#cats.Functor
[`Contravariant`]:http://typelevel.org/cats/api/#cats.functor.Contravariant
[`BiFunctor`]:http://typelevel.org/cats/api/#cats.functor.Bifunctor
[`Order`]:http://typelevel.org/cats/api/index.html#cats.package@Order[A]=cats.kernel.Order[A]
[`Show`]:http://typelevel.org/cats/api/index.html#cats.Show
[`Traverse`]:http://typelevel.org/cats/api/index.html#cats.Traverse
[`Monad`]:http://typelevel.org/cats/api/index.html#cats.Monad
[`Monoid`]:http://typelevel.org/cats/api/index.html#cats.package@Monoid[A]=cats.kernel.Monoid[A]
[`CellEncoder`]:{{ site.baseurl }}/api/#kantan.csv.package$$CellEncoder
[`CellDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package$$CellDecoder
[`RowDecoder`]:{{ site.baseurl }}/api/#kantan.csv.package$$RowDecoder
[`RowEncoder`]:{{ site.baseurl }}/api/#kantan.csv.package$$RowEncoder
[`ReadResult`]:{{ site.baseurl }}/api/#kantan.csv.package$$ReadResult
[`ParseResult`]:{{ site.baseurl }}/api/#kantan.csv.package$$ParseResult
[`DecodeResult`]:{{ site.baseurl }}/api/#kantan.csv.package$$DecodeResult
