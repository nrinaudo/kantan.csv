# scala-csv

[![Build Status](https://travis-ci.org/nrinaudo/scala-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scala-csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

A [scalaz-stream](./scalaz-stream) implementation is available as a separate module.


## Getting it

The current version is 0.1.2, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.2"
```


## Examples

The following examples all assume that an implicit `scala.io.Codec` is in scope, and that `com.nrinaudo._` has been
imported. That is:

```scala
import com.nrinaudo._

implicit val codec = scala.io.Codec.ISO8859
```

### Unsafe but efficient parsing
The various `unsafeRowsR` methods recycle a single instance of `ArrayBuffer`. While this is efficient, it's also very
unsafe and requires users to be careful not to store / modify that instance in any way.

```scala
csv.unsafeRowsR("input.csv", ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

### Safe parsing
The various `safeRowsR` methods create a new `Vector[String]` instance for each row. They represent a safer, if somewhat
less efficient alternative to `unsafeRowsR`.

```scala
csv.safeRowsR("input.csv", ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

### Typeclass-based parsing
If each row is to be turned into an object, consider declaring an implicit `RowReader` for your type.

For example:
```scala
implicit val userReader = RowReader(r => User(r(0), r(1)))

case class User(first: String, last: String)

csv.rowsR[User]("input.csv", ',')
```

This is how the `safeRowsR` methods are implemented under the hood.

Note that this approach can cause issues if the CSV data contains a header row. You can configure your `RowReader`
instance to skip the first line in the stream by overriding its `hasHeader` method (or by calling `withHeader`, which
will create a cloned instance that will skip the first row).

```scala
implicit val userReader = RowReader(r => User(r(0), r(1))).withHeader
```

### Writing lists of strings
Writing is achieved through one of the `rowsW` methods:

```scala
val out = csv.rowsW[List[String]](System.out, `,`)

out.write(List("aaa", "bbb", "ccc"))
out.write(List("zzz", "yyy", "xxx"))

out.close()
```


### Typeclass-based writing
Note that the previous example works because an implicit `RowWriter[List[String]]` is always in scope. Should you need
to write something other than lists of strings, you can create your own `RowWriter` instance:

```scala
implicit val userWriter = RowWriter(u => List(u.first, u.last))

csv.rowsW[User](System.out, `,`)
  .write(User("Locke", "Lamora"))
  .write(User("Nicolas", "Rinaudo"))
  .close()
```

CSV writing doesn't by default include a header row. This is configurable at the `RowWriter` level:
```scala
implicit val userWriter = RowWriter(u => List(u.first, u.last)).withHeader("First Name", "Last Name")
```