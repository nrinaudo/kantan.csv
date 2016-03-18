---
layout: default
title:  "Serialising collections as rows"
section: tutorial
status: wip
---

```tut:silent
import kantan.csv.ops._

val out = new java.io.StringWriter()
val writer = out.asCsvWriter[List[Int]](',')

writer.write(List(0, 1, 2)).write(List(3, 4, 5)).close()
```

```tut
println(out.toString())
```


