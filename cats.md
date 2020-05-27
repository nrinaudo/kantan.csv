---
layout: tutorial
title: "Cats module"
section: tutorial
sort_order: 23
---
Kantan.csv has a [cats](https://github.com/typelevel/cats) module that is, in its current incarnation, fairly bare
bones: it simply provides a few useful type class instances.

The `cats` module can be used by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-cats" % "0.6.1"
```

You then need to import the corresponding package:

```scala
import kantan.csv.cats._
```

## Cats instances

The following instance for cats type classes are provided:

* [`MonadError`] and [`SemigroupK`] for all decoders ([`CellDecoder`] and [`RowDecoder`]).
* [`Contravariant`] for all encoders ([`CellEncoder`] and [`RowEncoder`]).
* [`Show`] and [`Eq`] for all error types ([`ReadError`] and all its descendants).
* [`RowEncoder`] for any type that has a [`Foldable`].

[`MonadError`]:https://typelevel.org/cats/api/cats/MonadError.html
[`SemigroupK`]:https://typelevel.org/cats/api/cats/SemigroupK.html
[`SemigroupK`]:https://typelevel.org/cats/api/cats/Foldable.html
[`Show`]:https://typelevel.org/cats/api/cats/Show.html
[`Eq`]:https://typelevel.org/cats/api/cats/kernel/Eq.html
[`Contravariant`]:http://typelevel.org/cats/api/cats/Contravariant.html
[`CellEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$CellEncoder.html
[`CellDecoder`]:{{ site.baseurl }}/api/kantan/csv/CellDecoder$.html
[`RowDecoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowDecoder.html
[`RowEncoder`]:{{ site.baseurl }}/api/kantan/csv/package$$RowEncoder.html
[`ReadError`]:{{ site.baseurl }}/api/kantan/csv/ReadError.html
