# Getting Started
This part of the tutorial tries to give you a quick overview of the features you're likely to need. For most users, it
should be enough to get started, and likely to cover everything they ever need to know about working with CSV data.

## Getting the library
The current version is `0.1.4-SNAPSHOT`, which can be added to your project with the following line in your SBT build
file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.4-SNAPSHOT"
```


## Standard imports and implicits
This tutorial assumes the following imports are present:
```tut:silent
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
```

Additionally, most methods used to open CSV data for reading or writing expect an implicit `scala.io.Codec` to be in
scope. I'll be using `ISO-LATIN-1` here, but bear in mind that there is no single charset that will work for all CSV
data. Microsoft Excel, for instance, tends to change charset depending on the computer it's being executed on.

```tut:silent
implicit val codec = scala.io.Codec.ISO8859
```


## Reading CSV data
CSV data can come from a variety of sources. At the time of writing, the following ones are supported out of the box:
* `java.io.File`
* `java.io.InputStream`
* `String`
* `java.net.URL`
* `java.net.URI`
* `scala.io.Source`

Once you have an instance of one of these, you can simply turn it into an iterator on rows with the following code:
```tut:silent
// We're trying to open a wikipedia.csv resource.
val rawData: java.io.InputStream = getClass.getResourceAsStream("/wikipedia.csv")

rawData.asCsvRows[List[String]](',', false)
```
Note the two parameters:
* the first one is the character used to separate columns. It's often `,`, but can just as well be `;`, for example.
* the second one is a boolean that, if `true`, will treat the first row as a header and skip it.

The `asCsvRows` method also expects a type parameter: this is the type as which each row will be represented. In our
example, each row will be a list of strings.

You can, without any additional work, request rows to be represented as the following:
* any collection of any standard Scala type (ints, floats, booleans, options of one of these, eithers of two of these...).
* any tuple of standard Scala types.

For example, let's say we have the following data:
```csv
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

You could write:
```tut:silent
// Parses each row as a set of strings.
rawData.asCsvRows[Set[String]](',', false)

// Parses each row a more useful tuple 
rawData.asCsvRows[(Int, String, String, Option[String], Float)](',', true)
```

In this case though, the data would probably be better represented as a case class:
```tut:silent
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
```

Case classes are also supported, but require slightly more work: you need to create an instance of `RowDecoder` for
your case class. This is made easy by the various `RowDecoder.caseDecoderXXX` methods:

```tut:silent
implicit val carDecoder = RowDecoder.caseDecoder5(Car.apply)(1, 2, 0, 4, 3)
rawData.asCsvRows[Car](',', true)
```