---
layout: default
title:  "Writing CSV data"
section: tutorial
---

## Sample data
In this tutorial, we'll try to do the opposite as the [parsing one]({{ site.baseurl }}/tut/parsing.html): instead of
having CSV data to load in memory, we have the list of cars and need to write it out:

```tut:silent
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])

val data = List(Car("Ford", "E350", 1997, 3000F, Some("ac, abs, moon")),
                Car("Chevy", "Venture \"Extended Edition\"", 1999, 4900F, None),
                Car("Chevy", "Venture \"Extended Edition, Very Large\"", 1999, 5000F, None),
                Car("Jeep", "Grand Cherokee", 1996, 4799F, Some("MUST SELL!\nair, moon roof, loaded")))
```

## Setting up Tabulate
The code in this tutorial requires the following imports:

```tut:silent
import tabulate._     // Imports core classes.
import tabulate.ops._ // Enriches standard classes with CSV serialisation methods.
```


Additionally, most methods used to open CSV data for writing require an implicit `scala.io.Codec` to be in scope. I'll
be using `ISO-LATIN-1` here, but bear in mind no single charset will work for all CSV data.
Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```tut:silent
implicit val codec = scala.io.Codec.ISO8859
```


## The `CsvWriter` class
All CSV serialisation is done through the [`CsvWriter`] class. There are various ways of retrieving instances of that
class, the most common of which being to call [`asCsvWriter`] method that enriches any class that has an implicit
of instance of [`CsvOutput`] in scope.
 
[`asCsvWriter`] expects two value arguments:

* the column separator (the most common one being `,`)
* the optional CSV header.

More interestingly, [`asCsvWriter`] takes a type argument: the type of the elements that will be written. 



[`CsvWriter`]:{{ site.baseurl }}/api/#tabulate.CsvWriter
[`asCsvWriter`]:{{ site.baseurl }}/api/#tabulate.CsvOutput@writer[A](s:S,separator:Char,header:Seq[String])(implicitea:tabulate.RowEncoder[A],implicitengine:tabulate.engine.WriterEngine):tabulate.CsvWriter[A]