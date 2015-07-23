# scalaz-stream-csv

[scalaz-stream](https://github.com/scalaz/scalaz-stream) source for CSV data.


## Getting it

The current version is 0.1.1, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scalaz-stream-csv" % "0.1.1"
```


## Example

```scala
import scalaz.stream._
import scala.io.Codec

implicit val codec = Codec.ISO8859

csv.rowsR("input.csv", ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

Note that `rowsR` is safe, but allocates a new `Vector` for each row in the CSV data. `unsafeRowsR` re-uses a single
array buffer, which is more efficient but unsafe.
