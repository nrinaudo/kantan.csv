# kantan.csv

[![Build Status](https://github.com/nrinaudo/kantan.csv/actions/workflows/cli.yml/badge.svg?branch=master)](https://github.com/nrinaudo/kantan.csv/actions/workflows/cli.yml?branch=master)
[![kantan.csv Scala version support](https://index.scala-lang.org/nrinaudo/kantan.csv/kantan.csv/latest.svg)](https://index.scala-lang.org/nrinaudo/kantan.csv/kantan.csv)
[![Join the chat at https://gitter.im/nrinaudo/kantan.csv](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.csv)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

As much as possible, kantan.csv attempts to present a purely functional and safe interface to users. I've not hesitated
to violate these principles *internally* however, when it afforded better performances. This approach appears to be
[somewhat successful](https://nrinaudo.github.io/kantan.csv/benchmarks.html).

Documentation and tutorials are available on the [companion site](https://nrinaudo.github.io/kantan.csv/), but for those
looking for a few quick examples:

```scala
import java.io.File
import kantan.csv._         // All kantan.csv types.
import kantan.csv.ops._     // Enriches types with useful methods.
import kantan.csv.generic._ // Automatic derivation of codecs.

// Reading from a file: returns an iterator-like structure on (Int, Int)
new File("points.csv").asCsvReader[(Int, Int)](rfc)

// "Complex" types derivation: the second column is either an int, or a string that might be empty.
new File("dodgy.csv").asCsvReader[(Int, Either[Int, Option[String]])](rfc)

case class Point2D(x: Int, y: Int)

// Parsing the content of a remote URL as a List[Point2D].
new java.net.URL("http://someserver.com/points.csv").readCsv[List, Point2D](rfc.withHeader)

// Writing to a CSV file.
new File("output.csv").asCsvWriter[Point2D](rfc)
  .write(Point2D(0, 1))
  .write(Point2D(2, 3))
  .close()

// Writing a collection to a CSV file
new File("output.csv").writeCsv[Point2D](List(Point2D(0, 1), Point2D(2, 3)), rfc)
```

kantan.csv is distributed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).
