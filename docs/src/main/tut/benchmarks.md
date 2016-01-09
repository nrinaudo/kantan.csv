---
layout: default
title:  "Benchmarks"
---

## Benchmarked libraries

| Library               | Version |
|-----------------------|---------|
| [commons csv]         | 1.2     |
| [jackson csv]         | 2.6.4   |
| [opencsv]             | 3.6     |
| [product collections] | 1.4.2   |
| [univocity]           | 1.5.6   |
| tabulate              | 0.1.7   |

In order to be included in this benchmark, a library must be:

* reasonably popular
* reasonably easy to integrate
* able to both encode and decode some fairly straightforward, RFC compliant test data.

I allowed a small exception for [opencsv]: it does not actually decode CSV the way it should, but the misbehaviour is so
minor (quoted CRLFs are transformed in LFs) that I chose to disregard it.

One library that I wish I could have included is [purecsv](https://github.com/melrief/PureCSV), if only because 
there should be more pure Scala libraries in there. It failed my tests so utterly however that I had to disqualify it -
although the results were so bad that I believe they might be my fault rather than the library's.

Lastly, I've been in touch with someone from the [univocity] team that helped me identify what default settings I needed
to turn off for reasonable performances - it turns out that [univocity]'s defaults are great for huge CSV files and slow
IO, but not that good for small, in memory data sets. Moreover, it must be said that using [univocity]'s preferred
callback-based API rather than the iterator-like one yields significantly better results. I'm specifically benchmarking
iterator-like access however, and as such, am not using [univocity] in its optimised-for use case. That is to say,
the fact that it's not a clear winner in my benchmarks does not invalidate
[their own results](https://github.com/uniVocity/csv-parsers-comparison).

## Reading

| Library                | μs/action |
|------------------------|-----------|
| [commons csv]          | 59.05498  |
| [jackson csv]          | 27.939431 |
| [opencsv]              | 66.949113 |
| [product collections]  | 58.448646 |
| tabulate (commons-csv) | 63.764324 |
| tabulate (internal)    | 37.302322 |
| tabulate (jackson-csv) | 37.443351 |
| tabulate (opencsv)     | 76.193376 |
| [univocity]            | 45.873582 |


## Writing

| Library                | μs/action  |
|------------------------|------------|
| [commons csv]          | 29.190811  |
| [jackson csv]          | 24.465507  |
| [opencsv]              | 42.06396   |
| [product collections]  | 95.016553  |
| tabulate (commons-csv) | 33.309043  |
| tabulate (internal)    | 36.487753  |
| tabulate (jackson-csv) | 29.61176   |
| tabulate (opencsv)     | 47.803968  |
| [univocity]            | 506.966742 |

[commons csv]:https://commons.apache.org/proper/commons-csv/
[jackson csv]:https://github.com/FasterXML/jackson-dataformat-csv
[opencsv]:http://opencsv.sourceforge.net
[univocity]:https://github.com/uniVocity/univocity-parsers
[product collections]:https://github.com/marklister/product-collections