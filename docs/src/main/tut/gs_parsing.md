# Parsing CSV Data

```tut:invisible
import com.nrinaudo.csv._
import com.nrinaudo.csv.ops._
implicit val codec = scala.io.Codec.ISO8859
```

## Sample data

```csv
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
```

```tut:invisible
val rawData = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/wikipedia.csv")).mkString
```


## Rows as collections of strings

```tut
rawData.asCsvRows[List[String]](',', false).toList

rawData.asCsvRows[Set[String]](',', false).toList
```

Note the type: options instead of raw collections.

## Rows as tuples
No header handling:
```tut
rawData.asCsvRows[(Int, String, String, Option[String], Float)](',', false).toList
```

Avoid that first raw:

```tut
rawData.asCsvRows[List[String]](',', true).toList
```

For tutorial, use unsafe rows:

Header handling
```tut
rawData.asUnsafeCsvRows[(Int, String, String, Option[String], Float)](',', true).toList
```

## Rows as case classes

```tut
case class Car(make: String, model: String, year: Int, price: Float, desc: Option[String])
```

```tut
implicit val carDecoder = RowDecoder.caseDecoder5(Car.apply)(1, 2, 0, 4, 3)
rawData.asUnsafeCsvRows[Car](',', true).toList
```

```tut
rawData.asUnsafeCsvRows[Car](',', true).filter(_.desc.isDefined).maxBy(_.price)
```

## Advanced topics