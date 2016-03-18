---
layout: default
title:  "Serialising case classes as rows"
section: tutorial
status: wip
---

```tut:silent
import kantan.csv.ops._
import kantan.csv.generic._

case class Person(id: Int, name: String, age: Int)

val ps = List(Person(0, "Nicolas", 38), Person(1, "Kazuma", 1), Person(2, "John", 18))
```

```tut
ps.asCsv(',')
```

```tut:silent
import kantan.csv._

implicit val personEncoder: RowEncoder[Person] = RowEncoder.caseEncoder3(Person.unapply)(0, 1, 2) 
```

```tut
ps.asCsv(',')
```