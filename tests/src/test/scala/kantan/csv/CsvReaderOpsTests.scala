package kantan.csv

import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.ops._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class CsvReaderOpsTests extends FunSuite with GeneratorDrivenPropertyChecks {
  def asCsvReader[A: RowDecoder](data: List[RowValue[A]]): CsvReader[ReadResult[A]] =
    asCsv(data, ',').asCsvReader[A](',', false)

  test("CsvReader[ReadResult] instances should have a working mapResult method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ List[Float]) ⇒
      assert(asCsvReader(data).mapResult(f).toList == asCsvReader(data).map(_.map(f)).toList)
    }
  }

  test("CsvReader[ReadResult] instances should have a working flatMapResult method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ ReadResult[List[Float]]) ⇒
      assert(asCsvReader(data).flatMapResult(f).toList == asCsvReader(data).map(_.flatMap(f)).toList)
    }
  }

  test("CsvReader[ReadResult] instances should have a working filter method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ Boolean) ⇒
      assert(asCsvReader(data).filterResult(f).toList == asCsvReader(data).filter(_.exists(f)).toList)
    }
  }
}
