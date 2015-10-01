package com.nrinaudo.csv

import java.io.StringWriter

import com.nrinaudo.csv.scalacheck._
import com.nrinaudo.csv.ops._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

abstract class CsvWriterTest[A: RowFormat: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  def write(ss: List[A]): String = {
    val sw = new StringWriter()
    ss.foldLeft(sw.asCsvWriter[A](','))(_ write _).close()
    sw.toString
  }

  def read(str: String): List[A] = str.asUnsafeCsvRows[A](',', false).toList

  test("Serialized CSV data should be parsed correctly") {
    forAll { ss: List[A] =>
      val csv = write(ss)
      assert(read(csv) == ss)
    }
  }
}

object TestData {
  implicit val format = RowFormat.caseFormat7(TestData.apply, TestData.unapply)(1, 3, 0, 6, 2, 5, 4)

  implicit val arb = Arbitrary {
    for {
      f1 <- arbitrary[Int]
      f2 <- arbitrary[Float]
      f3 <- cell
      f4 <- arbitrary[Boolean]
      f5 <- arbitrary[Option[Long]]
      f6 <- arbitrary[BigInt]
      f7 <- arbitrary[Either[Option[Boolean], Double]]
    } yield TestData(f1, f2, f3, f4, f5, f6, f7)
  }
}

case class TestData(f1: Int, f2: Float, f3: String, f4: Boolean, f5: Option[Long], f6: BigInt, f7: Either[Option[Boolean], Double])
class TestDataSpec extends CsvWriterTest[TestData]
