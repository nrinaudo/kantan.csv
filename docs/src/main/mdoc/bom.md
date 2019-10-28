---
layout: tutorial
title: "Working with BOMs (and MS Excel)"
section: tutorial
sort_order: 31
---
Excel is unfortunately both the most commonly used software to view CSV data, and the worst software there is to view
CSV data. The main issue has to do with encoding - Excel will use the local system's default encoding, which changes
from one installation to another

The only way (that I know of) to force Excel to use the right encoding when opening a CSV file is to:

* encode it in `UTF-8` or `UTF-16LE` (other unicode encodings might work, but I've seen odd behaviours)
* add a [BOM](https://en.wikipedia.org/wiki/Byte_order_mark) to the file

Since version 0.1.18, kantan.csv has full support for BOMs, enabled by importing the following package:

```scala mdoc:silent
import kantan.codecs.resource.bom._
```

Once that's done, all IO operations performed by kantan.csv will be BOM aware:

* read operations will look for a BOM and, if one is found, use the corresponding character encoding over any that might
  have been specified by the caller.
* write operations will add a BOM whenever the character encoding used has one.

For example:

```scala mdoc:silent
import kantan.csv._
import kantan.csv.ops._
import scala.io.Codec

// Let kantan.csv know that data should be written in UTF-8
implicit val codec: Codec = Codec.UTF8

// Our input is in katakana, characters that cannot be encoded using ISO-LATIN-1.
val input = List("ニコラ", "リノド")

// File in which we'll be writing the CSV data.
val out = java.io.File.createTempFile("kantan.csv", "csv")

// Writes input using , as a column separator.
out.writeCsv(input, rfc)
```

Since we've imported `kantan.codecs.resource.bom._`, `out` contains the UTF-8 BOM. We can verify that by attempting
to read it with an incompatible encoding:

```scala mdoc
def readIso() = {
  // ISO-LATIN-1 cannot be used to read our file, since it does not support katakana.
  implicit val codec: Codec = Codec.ISO8859

  out.readCsv[List, String](rfc)
}

readIso()
```

Note that these behaviours are disabled by default: BOMs are advised against, and looking for them (and interpreting them
when found) has a performance cost.
