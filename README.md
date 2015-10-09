# Tabulate

[![Build Status](https://travis-ci.org/nrinaudo/tabulate.svg?branch=master)](https://travis-ci.org/nrinaudo/tabulate)
[![codecov.io](http://codecov.io/github/nrinaudo/tabulate/coverage.svg?branch=master)](http://codecov.io/github/nrinaudo/tabulate?branch=v0.1.4)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nrinaudo/tabulate_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/nrinaudo/tabulate_2.11)
[![Join the chat at https://gitter.im/nrinaudo/tabulate](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nrinaudo/tabulate)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

Documentation and tutorials are available on the [companion site](https://nrinaudo.github.io/tabulate/), but for those
looking for a few quick examples:

```scala
// Reading from a file: returns an Iterator[(Int, Int)]
new File("points.csv").asUnsafeCsvRows[(Int, Int)](',', false)

// "Complex" types derivation: the second column is either an int, or a string that might be empty.
new File("dodgy.csv").asUnsafeCsvRows[(Int, Either[Int, Option[String]])](',', false)

// Case class and its codec (encoder + decoder)
case class Point2D(x: Int, y: Int)
implicit val p2dCodec = RowCodec.caseCodec2(Point2D.apply, Point2D.unapply)(0, 1)

// Parsing as an Iterator[Point2D]
new java.net.URL("http://someserver.com/points.csv").asUnsafeCsvRows[Point2D](',', true)

// Writing to a CSV file.
new File("output.csv").asCsvWriter[Point2D](',')
  .write(Point2D(0, 1))
  .write(Point2D(2, 3))
  .close()
```

Tabulate is distributed under the [MIT License](http://opensource.org/licenses/mit-license.php).
