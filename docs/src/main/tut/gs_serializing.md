# Writing CSV data

```tut:invisible
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
implicit val codec = scala.io.Codec.ISO8859
```

```tut
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])

val data = List(Car("Ford", "E350", 1997, 3000F, Some("ac, abs, moon")),
                Car("Chevy", "Venture \"Extended Edition\"", 1999, 4900F, None),
                Car("Chevy", "Venture \"Extended Edition, Very Large\"", 1999, 5000F, None),
                Car("Jeep", "Grand Cherokee", 1996, 4799F, Some("MUST SELL!\nair, moon roof, loaded")))
```

## Writing collections of strings

```tut
val strOut = new java.io.StringWriter()
val ss = data.map(c => List(c.year.toString, c.make, c.model, c.desc.getOrElse(""), c.price.toString)) 

ss.foldLeft(strOut.asCsvWriter[Seq[String]](','))(_ write _).close

strOut.toString
```


## Writing tuples

```tut
val tupleOut = new java.io.StringWriter
val ts = data.map(c => (c.year, c.make, c.model, c.desc, c.price)) 

ts.foldLeft(tupleOut.asCsvWriter[(Int, String, String, Option[String], Float)](','))(_ write _).close

tupleOut.toString
```

## Writing case classes

```tut
implicit val carEncoder= RowEncoder.caseEncoder5(Car.unapply)(1, 2, 0, 4, 3)

val caseOut = new java.io.StringWriter

data.foldLeft(caseOut.asCsvWriter[Car](','))(_ write _).close

caseOut
```

## Advanced topics