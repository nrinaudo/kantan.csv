---
layout: tutorial
title: "Refined module"
section: tutorial
sort_order: 28
---
kantan.csv comes with a [refined](https://github.com/fthomas/refined) module that can be used
by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-refined" % "0.2.1"
```

You then need to import the corresponding package:

```tut:silent
import kantan.csv.refined._
```

And that's pretty much it. You can now encode and decode refined types directly.

Let's first set our types up:

```tut:silent
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import kantan.csv._
import kantan.csv.ops._

type PositiveInt = Int Refined Positive
```

We can then simply write the following:

```tut
"1,2".readCsv[List, List[PositiveInt]](rfc)

"1,-2".readCsv[List, List[PositiveInt]](rfc)
```
