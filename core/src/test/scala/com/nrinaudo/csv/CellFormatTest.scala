package com.nrinaudo.csv

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

abstract class CellFormatTest[A: CellFormat: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  test("Writing then reading data should leave it unchanged") {
    forAll { a: A => assert(CellReader[A].read(CellWriter[A].write(a)) == a)  }
  }
}

class StringFormatTest extends CellFormatTest[String]
class IntFormatTest extends CellFormatTest[Int]
class DoubleFormatTest extends CellFormatTest[Double]
class LongFormatTest extends CellFormatTest[Long]
class FloatFormatTest extends CellFormatTest[Float]
class ShortFormatTest extends CellFormatTest[Short]
class ByteFormatTest extends CellFormatTest[Byte]
class BooleanFormatTest extends CellFormatTest[Boolean]
class OptionFormatTest extends CellFormatTest[Option[Int]]