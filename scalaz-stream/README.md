# scalaz-stream-csv

[scalaz-stream](https://github.com/scalaz/scalaz-stream) source for CSV data.


## Getting it

The current version is 0.1.2, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scalaz-stream-csv" % "0.1.2"
```


## Examples

The following examples all assume that an implicit `scala.io.Codec` is in scope, and that `scalaz.stream._` has been 
imported. That is:

```scala
import scalaz.stream._

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
object User {
  implicit val rowReader = RowReader(r => User(r(0), r(1))
}

case class User(first: String, last: String)

csv.rowsR[User]("input.csv", ',')
```

This is how the `safeRowsR` methods are implemented under the hood.

Note that when using this approach, every single row in the CSV stream must be well formed. This essentially means that
typeclass-based parsing is incompatible with having a header row, or at least requires jumping through a few more hoops:

```scala
csv.unsafeRowsR("input.csv", ',')
  .drop(1)
  .map(RowReader[User].read)
```