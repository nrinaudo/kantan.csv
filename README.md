# scala-csv

[![Build Status](https://travis-ci.org/nrinaudo/scala-csv.svg?branch=master)](https://travis-ci.org/nrinaudo/scala-csv)

CSV is an unfortunate part of life. This attempts to alleviate the pain somewhat by letting developers treat CSV data
as a simple iterator.

A [scalaz-stream](./scalaz-stream) implementation is available as a separate module.


## Getting it
The current version is `0.1.4-SNAPSHOT`, which can be added to your project with the following line in your SBT build
file:

```scala
libraryDependencies += "com.nrinaudo" %% "scala-csv" % "0.1.4-SNAPSHOT"
```


## Examples

The following examples all assume that an implicit `scala.io.Codec` is in scope, and that `com.nrinaudo._` has been
imported. That is:

```scala
import com.nrinaudo._

// I'm using iso-8859-1 here because that's the encoding Excel uses when manipulating CSV
// files on my computer, but it might be very different on other setups.
implicit val codec = scala.io.Codec.ISO8859
```


## Reading
Reading CSV files is done through one of the various `rowsR` methods, whose return type depends on what you need each
row to be represented as. It'll always be an `Iterator` of something, but what that something is is entirely up to
the caller. The following sections explain how to control it.



### Reading rows as collections
The simplest way to read a CSV row is as a collection of a standard type - a `List[String]`, say. This is supported out
of the box by specifying the correct type argument to `rowsR`:

```scala
csv.rowsR[List[String]]("input.csv", ',')
```

All collection types are supported (well, technically, all collections that have a `CanBuildFrom` instance), as well as
the following standard types:

* `String`
* `Char`
* `Int`
* `Float`
* `Double`
* `Long`
* `Byte`
* `Short`
* `Boolean`
* `BigInt`
* `BigDecimal`
* `Option` of any of the above
* `Either` of any two of the above

Other types can be added - the process is fairly straightforward and explained in depth in a later section.


### Reading rows as tuples
Collections are nice, but they can only contain a single type. CVS rows tend to be made of composite types, and are
often better represented as tuples.

Tuples of any arity (up to 22, which as the time of writing is the maximum supported by scala) are supported out of the
box, and work just as with collections:

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

// The integer parameters tell caseReader what column to map to what field - the first
// integer is the index of the first User field, and so on.
implicit val userReader = RowReader.caseReader4(User.apply)(0, 1, 2, 3)

csv.rowsR[User]("users.csv", ',')
```

Note that should you need to both read and write instances of a case class, you might be better served by creating a
`RowFormat` than a `RowReader`.

### Reading rows as whatever the hell you want
These examples all use the same underlying mechanism: `rowsR` uses instances of the `RowReader` typeclass to turn a
CSV row into values of a more useful type.

Should you need to read rows in a way that isn't covered by one of the previous examples, you can always create your
own `RowReader` instance:

```scala
implicit val userReader = new RowReader[User] {
  // Alternatively, instead of calling toInt, you can use CellReader[Int].read.
  // More on that later.
  override def read(row: Seq[String]): User = User(row(0), row(1),
                                              row(2).toInt, row(3).toBoolean)
}

csv.rowsR[User]("users.csv", ',')
```

Note that should you need to both read and write values of your type, you might be better served by creating a
`RowFormat` than a `RowReader`.


### Reading individual cells
All the standard mechanisms for reading a CSV row rely on instances of the `CellReader` typeclass to parse individual
cells: as long as a type has an implicit `CellReader` instance in scope, it can be used as a case class field, tuple entry
or collection content type.

Should you require a type that isn't provided for by default, you can easily add support for it:

```scala
import java.util.Date
import java.text.SimpleDateFormat

implicit val dateReader = new CellReader[Date] {
  override def read(s: String): Date =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s)
}

csv.rowsR[List[Date]]("dates.csv", ',')
csv.rowsR[(Int, String, Date)]("input.csv", ',')
```

Note that should you need to both read and write values of your type, you might be better served by creating a
`CellFormat` than a `CellReader`.


### Trading safety for efficiency
At the lowest level, CSV parsing uses efficient but unsafe data structures - specifically, a single instance of 
`ArrayBuffer`, a mutable list. This is both a blessing and a curse: its mutability allows us to re-use it from one row
to the other, but it also makes things unsafe. Should callers store or modify that single instance in any way, there is
no telling what will break down the road.

Still, when efficiency is paramount, it's possible to use the various `unsafeRowsR` methods to expose the underlying
`ArrayBuffer[String]` and avoid creating new structures for each row. This is of course not compatible with the various
mechanisms described above - you'll only get values of type `ArrayBuffer[String]` and will need to write your own
cell / row parsing code if necessary.
 
 
 
## Writing
Writing is achieved through one of the various `rowsW` methods. Just as with reading, writing tries to be as flexible
as possible in the types that can handled.



### Writing sequences
Default implementations are provided for all sequence types (anything that extends `Seq`) of most standard types:

```scala
val out = csv.rowsW[List[Int]](System.out, ',')
  .write(List(1, 2, 3))
  .write(List(4, 5, 6))
  .close()
```

The following standard types are supported by default:

* `String`
* `Char`
* `Int`
* `Float`
* `Double`
* `Long`
* `Byte`
* `Short`
* `Boolean`
* `BigInt`
* `BigDecimal`
* `Option` of any of the above
* `Either` of any two of the above

Adding more data types is straightforward and detailed in a later section.


### Writing tuples
CSV rows are seldom composed of a unique type, and are often better represented as tuples. Tuples of arity 1 to 22 are
supported out of the box:

```scala
val out = csv.rowsW[(Int, String, Boolean)](System.out, ',')
  .write((1, "a", true))
  .write((2, "b", false))
  .close()
```

The same standard types are supported as for sequences.


### Writing case classes
You'll often find yourself dealing with specialised case classes rather than more generic tuples. These are supported as
well, although they require a bit more work: you need to create a dedicated instance of `RowWriter` and bring it as
implicit in scope.

```scala
case class User(first: String, last: String, age: Int, female: Boolean)

// The integer parameters tell caseWriter what field to map to what column - the first
// integer is the index of the first User field, and so on.
implicit val userWriter = RowReader.caseReader4(User.unapply)(0, 1, 2, 3)

val out = csv.rowsW[User]("users.csv", ',')
  .write(User("Steve", "Jones", 22, false))
  .write(User("Jane", "Doe", 33, true))
  .close()
```

Note that should you need to both read and write values of your case class, you might be better served by creating a
`RowFormat` than a `RowReader`.

### Writing anything else
These examples all rely on the same underlying mechanism: `rowsW` expects an implicit `RowWriter` instance for the
relevant types to be in scope. Should you need to writes values of a type that is not a collection, a tuple or a
case class, you can always create your own `RowWriter`:

```scala
implicit val userWriter = new RowWriter[User] {
  // Alternatively, instead of calling toString, you can use CellWriter[Int].write(u.age).
  // More on that later.
  override def write(u: User): Seq[String] =
    List(u.first, u.last, u.age.toString, u.female.toString)
}

csv.rowsW[User]("users.csv", ',')
  .write(User("Steve", "Jones", 22, false))
  .write(User("Jane", "Doe", 33, true))
  .close()
```

Note that should you need to both read and write values of your type, you might be better served by creating a
`RowFormat` than a `RowReader`.

### Writing individual cells
All the previous mechanisms for writing CSV rows rely on instances of the `CellWriter` typeclass to write individual
cells: as long as a type has on implicit `CellWriter` in scope, it can be used as a case class field, tuple entry
or collection content type.

Should you require a type that isn't provided for by default, you can easily add support for it:

```scala
import java.util.Date
import java.text.SimpleDateFormat

implicit val dateWriter = new CellWriter[Date] {
  override def read(d: Date): String =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d)
}

csv.rowsW[(String, Int, Date)](System.out, ',')
  .write(("a", 1, date1))
  .write(("b", 2, date2))
  .close()
```

Note that should you need to both read and write values of your type, you might be better served by creating a
`CellFormat` than a `CellReader`.


## Reading *and* writing: formats

### Row formats
The `RowFormat` typeclass brings `RowReader` and `RowWriter` together. It's a useful shortcut for when you'll need to
create both a reader and a writer anyway:

```scala
// This'll allow you to read and write instances of User from and to CSV streams.
implicit val userFormat = RowFormat.caseFormat4(User.apply, User.unapply)(0, 1, 2, 3)
```

### Cell formats
Similarly, `CellFormat` brings `CellReader` and `CellWriter` together:

```scala
import java.util.Date
import java.text.SimpleDateFormat

implicit val dateFormat = CellFormat[Date](
  s => new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s),
  d => new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d)
)
```