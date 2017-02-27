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
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "0.1.18"
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

[`Functor`]:http://typelevel.org/cats/api/cats/Functor.html
[`Contravariant`]:http://typelevel.org/cats/api/cats/functor/Contravariant.html
[`BiFunctor`]:http://typelevel.org/cats/api/cats/functor/Bifunctor.html
[`Order`]:http://typelevel.org/cats/api/cats/kernel/Order.html
[`Show`]:http://typelevel.org/cats/api/cats/Show.html
[`Traverse`]:http://typelevel.org/cats/api/cats/Traverse.html
[`Monad`]:http://typelevel.org/cats/api/cats/Monad.html
[`Monoid`]:http://typelevel.org/cats/api/cats/kernel/Monoid.html
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`ReadResult`]:{{ site.baseurl }}/api/kantan/csv/ReadResult$.html
[`ParseResult`]:{{ site.baseurl }}/api/kantan/csv/ParseResult$.html
[`DecodeResult`]:{{ site.baseurl }}/api/kantan/csv/DecodeResult$.html
