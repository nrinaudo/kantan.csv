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
