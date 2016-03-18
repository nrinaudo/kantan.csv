---
layout: default
title:  "Serialising one row at a time"
section: tutorial
status: wip
---

```tut:silent
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)
```

```tut:silent
val out = new java.io.StringWriter()

val writer = out.asCsvWriter[Person](',', List("Id", "Name", "Age"))

writer.write(Person(0, "Nicolas", 38)).
       write(Person(1, "Kazuma", 1)).
       write(Person(2, "John", 18)).
       close()
```

```tut
println(out.toString())
```

