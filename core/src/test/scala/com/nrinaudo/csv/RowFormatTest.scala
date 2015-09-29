package com.nrinaudo.csv

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import RowFormatTest._

object RowFormatTest {
  implicit def tuple1[A: Arbitrary]: Arbitrary[Tuple1[A]] = Arbitrary(Arbitrary.arbitrary[A].map(Tuple1.apply))
}

abstract class RowFormatTest[R: RowFormat: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  test("Writing then reading data should leave it unchanged") {
    forAll { r: R =>
      assert(RowReader[R].read(RowWriter[R].write(r)) == r)
    }
  }
}

class ListFormatTest extends RowFormatTest[List[Int]]
class VectorFormatTest extends RowFormatTest[Vector[Int]]
class SeqFormatTest extends RowFormatTest[Seq[Int]]
class StreamFormatTest extends RowFormatTest[Stream[Int]]
class Tuple1FormatTest extends RowFormatTest[Tuple1[Int]]
class Tuple2FormatTest extends RowFormatTest[(Int, Int)]
class Tuple3FormatTest extends RowFormatTest[(Int, Int, Int)]
class Tuple4FormatTest extends RowFormatTest[(Int, Int, Int, Int)]
class Tuple5FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int)]
class Tuple6FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int)]
class Tuple7FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int)]
class Tuple8FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple9FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple10FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple11FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple12FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple13FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple14FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple15FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int)]
class Tuple16FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int)]
class Tuple17FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int)]
class Tuple18FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int)]
class Tuple19FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int)]
class Tuple20FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int)]
class Tuple21FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int, Int)]
class Tuple22FormatTest extends RowFormatTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int, Int, Int)]