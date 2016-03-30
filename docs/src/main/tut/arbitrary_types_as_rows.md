---
layout: default
title:  "Serialising arbitrary types as rows"
section: tutorial
status: wip
sort: 14
---

```tut:silent
class Person(val id: Int, val name: String, val age: Int)

val ps = List(new Person(0, "Nicolas", 38), new Person(1, "Kazuma", 1), new Person(2, "John", 18))
```

```tut:silent
import kantan.csv._
import kantan.csv.ops._


implicit val personEncoder = RowEncoder.encoder3((p: Person) ⇒ (p.id, p.name, p.age))(0, 2, 1)
```

```tut
ps.asCsv(',')
```