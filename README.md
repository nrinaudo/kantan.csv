# kantan.csv

[![Build Status](https://travis-ci.org/nrinaudo/kantan.csv?branch=master)](https://travis-ci.org/nrinaudo/kantan.csv)
[![codecov.io](http://codecov.io/github/nrinaudo/kantan.csv/coverage.svg?branch=master)](http://codecov.io/github/nrinaudo/kantan.csv)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.csv_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.csv_2.11)
[![Join the chat at https://gitter.im/nrinaudo/kantan.csv](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.
 
As much as possible, kantan.csv attempts to present a purely functional and safe interface to users. I've not hesitated
to violate these principles *internally* however, when it afforded better performances. This approach appears to be
[somewhat successful](https://nrinaudo.github.io/kantan.csv/tut/benchmarks.html).

Documentation and tutorials are available on the [companion site](https://nrinaudo.github.io/kantan.csv/), but for those
looking for a few quick examples:

```scala
import java.io.File
import kantan.csv._                // All kantan.csv types.
import kantan.csv.ops._            // Enriches types with useful methods.
import kantan.csv.generic.codecs._ // Automatic derivation of codecs for case classes.

// Reading from a file: returns an Iterator[(Int, Int)]
new File("points.csv").asUnsafeCsvReader[(Int, Int)](',', false)

// "Complex" types derivation: the second column is either an int, or a string that might be empty.
new File("dodgy.csv").asUnsafeCsvReader[(Int, Either[Int, Option[String]])](',', false)

case class Point2D(x: Int, y: Int)

// Parsing the content of a remote URL as a List[Point2D].
new java.net.URL("http://someserver.com/points.csv").readCsv[List, Point2D](',', true)

// Writing to a CSV file.
new File("output.csv").asCsvWriter[Point2D](',')
  .write(Point2D(0, 1))
  .write(Point2D(2, 3))
  .close()
```

kantan.csv is distributed under the [MIT License](http://opensource.org/licenses/mit-license.php).
