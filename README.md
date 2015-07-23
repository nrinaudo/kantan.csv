# scala-csv

[![Build Status](https://travis-ci.org/nrinaudo/scala-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scala-csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.
 
A [scalaz-stream](./scalaz-stream) implementation is available as a separate module. 


## Getting it

The current version is 0.1.0, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.0"
```


## Example

```scala
import com.nrinaudo._
import scala.io.Codec

implicit codec = Codec.ISO8859

csv.safe("input.csv", ',')
  .drop(1)          // Drop the header
  .map(_(0).toLong) // Discard all but the first column, which is a long
```

All `safe` methods use immutable structures, at the cost of allocating a new `Vector` for each row. The `unsafe`
methods, on the other hand, only allocate a single `ArrayBuffer` - they are more efficient but might result in corrupt
data should the buffer be modified / stored as is by callers.