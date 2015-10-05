# scalaz-stream-csv

[scalaz-stream](https://github.com/scalaz/scalaz-stream) source for CSV data.


## Getting it

The current version is 0.1.3, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scalaz-stream-csv" % "0.1.3"
```


## Documentation

The API is very similar to the [core](../) one. The only specificity is that:
* `unsafeRowsR` returns instances of `Process[Task, ArrayBuffer[String]]`.
* `rowsR` returns instances of `Process[Task, A]`.
* `rowsW` returns instances of `Sink[Task, A]`.

Aside from that, the same type classes are used for parsing / serialization, and everything should behave the same.

