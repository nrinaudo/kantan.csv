---
layout: default
title:  "Serialising tuples as rows"
section: tutorial
status: wip
---
```tut:silent
import kantan.csv.ops._

val out = new java.io.StringWriter()
val writer = out.asCsvWriter[(Int, Float, Boolean)](',')

writer.write((0, 1F, false)).write((3, 4F, true)).close()
```

```tut
println(out.toString())
```


