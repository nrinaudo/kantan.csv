---
layout: default
title:  "Serialising entire collections"
section: tutorial
status: wip
---

```tut:silent
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

```tut:silent
val out = new java.io.StringWriter()

out.writeCsv(ps, ',', List("Id", "Name", "Age"))
```

```tut
println(out.toString())
```

