---
layout: scala mdocorial
title: "Libra module"
section: scala mdocorial
sort_order: 30
---

kantan.csv comes with a [libra](https://github.com/to-ithaca/libra) module that can be used
by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-libra" % "0.6.0"
```

You then need to import the corresponding package:

```scala
import kantan.csv.libra._
```

And that's pretty much it. You can now encode and decode refined types directly.

Let's first set our types up:

```scala
import libra._
import kantan.csv._
import kantan.csv.ops._

type Distance = QuantityOf[Int, Length, Metre]
type Duration = QuantityOf[Int, Time, Second]
```

We can then simply write the following:

```scala
"1,2".readCsv[List, (Distance, Duration)](rfc)
// res0: List[ReadResult[(Distance, Duration)]] = List(
//   Right((Quantity(1), Quantity(2)))
// )
```

