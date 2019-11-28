---
layout: default
title: "Benchmarks"
---

## Benchmarked libraries

| Library               | Version  |
|-----------------------|----------|
| [commons csv]         |      1.7 |
| [jackson csv]         |   2.9.10 |
| [opencsv]             |      5.0 |
| [scala csv]           |    1.3.6 |
| kantan.csv            |    0.6.1 |
| [uniVocity]           |    2.8.3 |

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

| Library                  |  μs/action |
|--------------------------|------------|
| [commons csv]            |      49.40 |
| [jackson csv]            |      24.00 |
| kantan.csv (commons csv) |      76.37 |
| kantan.csv (internal)    |     101.62 |
| kantan.csv (jackson csv) |      44.59 |
| [opencsv]                |      68.77 |
| [scala csv]              |     117.41 |
| [uniVocity]              |      28.16 |

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
| [commons csv]            |     25.63 |
| [jackson csv]            |     20.50 |
| kantan.csv (commons csv) |     30.43 |
| kantan.csv (internal)    |     29.90 |
| kantan.csv (jackson csv) |     30.25 |
| [opencsv]                |     50.27 |
| [scala csv]              |     41.04 |
| [uniVocity]              |     29.19 |

[commons csv]:https://commons.apache.org/proper/commons-csv/
[jackson csv]:https://github.com/FasterXML/jackson-dataformat-csv
[opencsv]:http://opencsv.sourceforge.net
[scala csv]:https://github.com/tototoshi/scala-csv
[uniVocity]:https://github.com/uniVocity/uniVocity-parsers
[product collections]:https://github.com/marklister/product-collections
