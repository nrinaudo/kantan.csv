---
layout: scala mdocorial
title: "Refined module"
section: scala mdocorial
sort_order: 28
---

kantan.csv comes with a [refined](https://github.com/fthomas/refined) module that can be used
by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-refined" % "0.6.0"
```

You then need to import the corresponding package:

```scala
import kantan.csv.refined._
```

And that's pretty much it. You can now encode and decode refined types directly.

Let's first set our types up:

```scala
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import kantan.csv._
import kantan.csv.ops._

type PositiveInt = Int Refined Positive
```

We can then simply write the following:

```scala
"1,2".readCsv[List, List[PositiveInt]](rfc)
// res0: List[ReadResult[List[PositiveInt]]] = List(Right(List(1, 2)))

"1,-2".readCsv[List, List[PositiveInt]](rfc)
// res1: List[ReadResult[List[PositiveInt]]] = List(
//   Left(TypeError("Not acceptable: 'Predicate failed: (-2 > 0).'"))
// )
```

