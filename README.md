# scala-csv

[![Build Status](https://travis-ci.org/nrinaudo/scala-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scala-csv)
[![codecov.io](http://codecov.io/github/nrinaudo/scala-csv/coverage.svg?branch=master)](http://codecov.io/github/nrinaudo/scala-csv?branch=master)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

A [scalaz-stream](./scalaz-stream) implementation is available as a separate module.


## Getting it

The current version is 0.1.3, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.3"
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
If safety is an issue (and efficiency is not), the `rowsR` methods can be used as follows:

```scala
csv.rowsR[Vector[String]]("input.csv", ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

Implementations are available for `Vector` and `List`.

### Typeclass-based parsing
The previous example is a specific usage of a more generic mechanism: the `RowReader` typeclass, which allows you to
declare implicit parsers for any type:
```scala
implicit val userReader = RowReader(r => User(r(0), r(1)))

case class User(first: String, last: String)

csv.rowsR[User]("input.csv", ',')
```

Note that this approach can cause issues if the CSV data contains a header row. You can configure your `RowReader`
instance to skip the first line in the stream by overriding its `hasHeader` method (or by calling `withHeader`, which
will create a cloned instance that will skip the first row).

```scala
val userReaderH = userReader.withHeader
```

### Writing lists of strings
Writing is achieved through one of the `rowsW` methods:

```scala
val out = csv.rowsW[List[String]](System.out, ',')

out.write(List("aaa", "bbb", "ccc"))
out.write(List("zzz", "yyy", "xxx"))

out.close()
```


### Typeclass-based writing
The previous example works because an implicit `RowWriter[List[String]]` is always in scope. Should you need to write 
something other than lists of strings, you can create your own `RowWriter` instance:

```scala
implicit val userWriter = RowWriter((u: User) => List(u.first, u.last))

csv.rowsW[User](System.out, ',')
  .write(User("Locke", "Lamora"))
  .write(User("Nicolas", "Rinaudo"))
  .close()
```

CSV writing doesn't by default include a header row. This is configurable at the `RowWriter` level:
```scala
val userWriterH = userWriter.withHeader("First Name", "Last Name")
```