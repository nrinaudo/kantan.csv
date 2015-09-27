# scala-csv

[![Build Status](https://travis-ci.org/nrinaudo/scala-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scala-csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

A [scalaz-stream](./scalaz-stream) implementation is available as a separate module.


## Getting it
The current version is `0.1.4-SNAPSHOT`, which can be added to your project with the following line in your SBT build file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.4-SNAPSHOT"
```


## Examples

The following examples all assume that an implicit `scala.io.Codec` is in scope, and that `com.nrinaudo._` has been
imported. That is:

```scala
import com.nrinaudo._

// I'm using iso-8859-1 here because that's the encoding Excel uses when manipulating CSV files on my computer, but it
// might be very different on other setups.
implicit val codec = scala.io.Codec.ISO8859
```


## Reading
Reading CSV files is done through one of the various `rowsR` methods, whose return type depend on what you need each
row to be represented as. It'll always be an `Iterator` of something, but what that something is is entirely up to
the caller. The following sections explain how to control this.


### Reading rows as collections
The simplest way to read a CSV row is as a collection of a standard type - a `List[String]`, say. This is supported out
of the box by specifying the correct type argument to `rowsR`:

```scala
csv.rowsR[List[String]]("input.csv", ',')
```

All collection types are supported (well, technically, all collections that have a `CanBuildFrom` instance), as well as
the following standard types:

* String
* Int
* Float
* Double
* Long
* Byte
* Short
* Boolean

Other types can be added - the process is fairly straightforward and explained in depth in a later section.


### Reading rows as tuples
Collections are nice, but they can only contain a single type. CVS rows tend to be made of composite types, and are
often better represented as tuples.

Tuples of any arity (up to 22, which as the time of writing is the maximum supported by scala) are supported out of the
box, and works just as with collections:

```scala
csv.rowsR[(String, Int, Float, Boolean)]("input.csv", ',')
```

As with collections, standard scala types have native support, and others can be added as necessary.


### Reading rows as case-classes
While tuples can be convenient, it's often desirable to read CSV rows as values of a more specific type. A CSV file
might contain user information, for example, and each row could be turned into instances of case class `User`.

This is somewhat less straightforward than tuples or collections, as implicit readers cannot be derived automatically.
You can, however, easily create them by calling `RowReader.caseReaderXXX`, where `XXX` is the number of fields contained
by the case class. Arity 1 to 22 are supported, which covers the whole range of possible case classes at the time of
writing.

```scala
case class User(first: String, last: String, age: Int, female: Boolean)

// The integer parameters tell caseReader what column to map to what field - the first integer is the index of the first
// User field, and so on.
implicit val userReader = RowReader.caseReader4(User.apply)(0, 1, 2, 3)

csv.rowsR[User]("users.csv", ',')
```

### Reading rows as whatever the hell you want
These examples all use the same underlying mechanism: `rowsR` uses instances of the `RowReader` typeclass to turn a
CSV row into values of a more useful type.

Should you need to read rows in a way that isn't covered by one of the previous examples, you can always create your
own `RowReader` instance:

```scala
implicit val userReader = new RowReader[User] {
  // Alternatively, instead of calling toInt, you can use RowCell[Int].read. More on that later.
  override def read(row: Seq[String]): User = User(row(0), row(1), row(2).toInt, row(3).toBoolean)
}

csv.rowsR[User]("users.csv", ',')
```

### Reading individual cells
All the standard mechanisms for reading a CSV row rely on instances of the `CellReader` typeclass to parse individual
cell: as long as a type as an implicit `CellReader` instance in scope, it can be used as a case class field, tuple entry
or collection content type.

Should you require a type that isn't provided for by default, you can easily add support for it:

```scala
import java.util.Date
import java.text.SimpleDateFormat

implicit val dateReader = new CellReader[Date] {
  override def read(s: String): Date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s)
}

csv.rowsR[List[Date]]("dates.csv", ',')
```

### Trading safety for efficiency
At the lowest level, CSV parsing uses efficient but unsafe data structures - specifically, a single instance of 
`ArrayBuffer`, a mutable list. This is both a blessing and a curse: its mutability allows us to re-use it from one row
to the other, but it also makes things unsafe. Should callers store or modify that single instance in any way, there is
no telling what will break down the road.

Still, when efficiency is paramount, it's possible to use the various `unsafeRowsR` methods to expose the underlying
`ArrayBuffer[String]` and avoid creating new structures for each row. This is of course not compatible with the various
mechanisms described above - you'll only get values of type `ArrayBuffer[String]` and will need to write your own
cell / row parsing code if necessary. 


### Writing lists of strings
Writing is achieved through one of the `rowsW` methods:

```scala
val out = csv.rowsW[List[String]](System.out, ',')

out.write(List("aaa", "bbb", "ccc"))
out.write(List("zzz", "yyy", "xxx"))

out.close()
```


### Typeclass-based writing
The previous example works because an implicit `RowWriter[List[String]]` is always in scope. Should you need to write 
something other than lists of strings, you can create your own `RowWriter` instance:

```scala
implicit val userWriter = RowWriter((u: User) => List(u.first, u.last))

csv.rowsW[User](System.out, ',')
  .write(User("Locke", "Lamora"))
  .write(User("Nicolas", "Rinaudo"))
  .close()
```

CSV writing doesn't by default include a header row. This is configurable at the `RowWriter` level:
```scala
val userWriterH = userWriter.withHeader("First Name", "Last Name")
```


### Writing tuples or typeclasses
Just like you can use `RowReader.caseReaderXXX` to derive readers for case classes, 