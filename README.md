# scalaz-csv

[![Build Status](https://travis-ci.org/nrinaudo/scalaz-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scalaz-csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a [scalaz-stream](https://github.com/scalaz/scalaz-stream) source.


## Getting it

The current version is 0.1.0, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scalaz-csv" % "0.1.0"
```


## Example

```scala
import com.nrinaudo._
import scala.io.{Source, Codec}

csv.rowsR(Source.fromFile("input.csv")(Codec.ISO8859), ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

Note that `rowsR` is safe, but allocates a new `Vector` for each row in the CSV data. `unsafeRowsR` re-uses a single
array buffer, which is more efficient but unsafe.