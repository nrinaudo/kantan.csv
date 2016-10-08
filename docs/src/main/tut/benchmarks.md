---
layout: default
title: "Benchmarks"
---

## Benchmarked libraries

| Library               | Version  |
|-----------------------|----------|
| [commons csv]         |     1.4  |
| [jackson csv]         |   2.8.2  |
| [opencsv]             |     3.8  |
| [product collections] |   1.4.3  |
| [scala csv]           |   1.3.3  |
| kantan.csv            |   0.1.10 |
| [uniVocity]           |   2.2.1  |

In order to be included in this benchmark, a library must be:

* reasonably popular
* reasonably easy to integrate
* able to both encode and decode some fairly straightforward, RFC compliant test data.

The first two are purely subjective, but I have actual
[tests](https://github.com/nrinaudo/kantan.csv/tree/master/benchmark/src/test/scala/kantan/csv/benchmark) to back the third
condition, and have disqualified some libraries that I could not get to pass them.

### opencsv
[opencsv] is an exception to these rules: it does not actually pass the RFC compliance tests. The misbehaviour is so
minor (quoted CRLFs are transformed in LFs) that I chose to disregard it, however.

### PureCSV
One library that I wish I could have included is [PureCSV](https://github.com/melrief/PureCSV), if only because
there should be more pure Scala libraries in there. It failed my tests so utterly however that I had to disqualify it -
although the results were so bad that I believe they might be my fault rather than the library's. I'll probably give it
another go for a later benchmark and try to see if I can work around the issues.

### uniVocity
uniVocity was almost disqualified from the benchmarks because initial performances were atrocious.

I've been in touch with someone from their team though, and he helped me identify what default settings I needed
to turn off for reasonable performances - it turns out that [uniVocity]'s defaults are great for huge CSV files and slow
IO, but not that good for small, in-memory data sets.

Moreover, it must be said that using [uniVocity]'s preferred callback-based API yields significantly better results than
the iterator-like one. I'm specifically benchmarking iterator-like access however, and as such not using [uniVocity]
in its optimised-for use case. That is to say, the fact that it's not a clear winner in my benchmarks does not
invalidate [their own results](https://github.com/uniVocity/csv-parsers-comparison).

## Benchmark tool
All benchmarks were executed through [jmh](http://openjdk.java.net/projects/code-tools/jmh/), a fairly powerful tool
that helps mitigate various factors that can make results unreliable - unpredictable JIT optimisation, lazy JVM
initialisations, ...

The one thing I couldn't control or alternate was the order in which the benchmarks were executed: jmh does it
alphabetically. Given that [jackson csv] is always executed second and still gets the best results by far, I'm assuming
that's not much of an issue.

## Reading
Reading is benchmarked by repeatedly parsing a known, simple, RFC-compliant
[input](https://github.com/nrinaudo/kantan.csv/blob/master/benchmark/src/main/scala/kantan/csv/benchmark/package.scala).

Results are expressed in μs/action, where and action is a complete read of the sample input. This means that the lower
the number, the better the results.

| Library                  | μs/action |
|--------------------------|-----------|
| [commons csv]            |     56.15 |
| [jackson csv]            |     26.64 |
| kantan.csv (commons csv) |     74.34 |
| kantan.csv (internal)    |     48.91 |
| kantan.csv (jackson csv) |     44.37 |
| kantan.csv (opencsv)     |     81.45 |
| [opencsv]                |     64.23 |
| [product collections]    |     52.85 |
| [scala csv]              |    155.87 |
| [uniVocity]              |     29.27 |

A few things are worth pointing out:

* [jackson csv] is frighteningly fast.
* [uniVocity] is being used in a context for which it's known to have suboptimal performances, and still has one of the
  better results.
* kantan.csv's internal parser has pretty decent parsing performances, all things considered.


## Writing
Writing is benchmarked in a symmetric fashion to reading: the same data is used, but instead of being parsed, it's being
serialized.

| Library                  | μs/action |
|--------------------------|-----------|
| [commons csv]            |     27.59 |
| [jackson csv]            |     23.16 |
| kantan.csv (commons csv) |     32.63 |
| kantan.csv (internal)    |     29.96 |
| kantan.csv (jackson csv) |     26.91 |
| kantan.csv (opencsv)     |     65.50 |
| [opencsv]                |     55.38 |
| [product collections]    |     91.63 |
| [scala csv]              |     54.79 |
| [uniVocity]              |     31.35 |

[commons csv]:https://commons.apache.org/proper/commons-csv/
[jackson csv]:https://github.com/FasterXML/jackson-dataformat-csv
[opencsv]:http://opencsv.sourceforge.net
[scala csv]:https://github.com/tototoshi/scala-csv
[uniVocity]:https://github.com/uniVocity/uniVocity-parsers
[product collections]:https://github.com/marklister/product-collections
