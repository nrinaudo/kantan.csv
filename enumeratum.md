---
layout: tutorial
title: "Enumeratum module"
section: tutorial
sort_order: 29
---
kantan.csv comes with an [enumeratum](https://github.com/lloydmeta/enumeratum) module that can be used
by adding the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.nrinaudo" %% "kantan.csv-enumeratum" % "0.6.0"
```

## Name-based enumerations

When working with enumerations of type `Enum`, you should import the following package:

```scala
import kantan.csv.enumeratum._
```

And that's pretty much it. You can now encode and decode your enumeration directly.

Let's first set our types up:

```scala
import enumeratum._

sealed trait DummyEnum extends EnumEntry

object DummyEnum extends Enum[DummyEnum] {

  val values = findValues

  case object Hello   extends DummyEnum
  case object GoodBye extends DummyEnum
  case object Hi      extends DummyEnum

}
```

And a few further imports, to bring our enumeration and the kantan.csv syntax in scope:

```scala
import kantan.csv.rfc
import kantan.csv.ops._
```


We can then simply write the following:

```scala
"Hello,GoodBye".readCsv[List, List[DummyEnum]](rfc)
// res0: List[kantan.csv.package.ReadResult[List[DummyEnum]]] = List(
//   Right(List(Hello, GoodBye))
// )

"Hello,GoodDay".readCsv[List, List[DummyEnum]](rfc)
// res1: List[kantan.csv.package.ReadResult[List[DummyEnum]]] = List(
//   Left(
//     TypeError("'GoodDay' is not a member of enumeration [Hello, GoodBye, Hi]")
//   )
// )
```



## Value-based enumerations

For enumerations of type `ValueEnum`, you should import the following package:

```scala
import kantan.csv.enumeratum.values._
```

And that's pretty much it. You can now encode and decode your enumeration directly.

Let's first set our types up:

```scala
import enumeratum.values._

sealed abstract class Greeting(val value: Int) extends IntEnumEntry

object Greeting extends IntEnum[Greeting] {

  val values = findValues

  case object Hello   extends Greeting(1)
  case object GoodBye extends Greeting(2)
  case object Hi      extends Greeting(3)
  case object Bye     extends Greeting(4)

}
```

And a few further imports, to bring our enumeration and the kantan.csv syntax in scope:

```scala
import kantan.csv.rfc
import kantan.csv.ops._
```

We can then simply write the following:

```scala
"1,2".readCsv[List, List[Greeting]](rfc)
// res3: List[kantan.csv.package.ReadResult[List[Greeting]]] = List(
//   Right(List(Hello, GoodBye))
// )

"1,-2".readCsv[List, List[Greeting]](rfc)
// res4: List[kantan.csv.package.ReadResult[List[Greeting]]] = List(
//   Left(TypeError("'-2' is not in values [1, 2, 3, 4]"))
// )
```
