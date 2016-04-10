package kantan.csv

import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.laws.CodecValue
import kantan.csv.ops._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.util.Try

class CsvInputOpsTests  extends FunSuite with GeneratorDrivenPropertyChecks {
  type CsvRow[A] = CodecValue[Seq[String], Seq[A]]

  def toCsv[A](data: List[CsvRow[A]]): String = data.map(_.encoded).asCsv(',')

  def compare[F](csv: List[Result[F, List[Int]]], data: List[CsvRow[Int]]): Boolean = {
    if(csv.length != data.length) false
    else csv.zip(data).forall {
      case (Success(is), CodecValue.LegalValue(_, cs)) ⇒ is == cs
      case (Failure(_), CodecValue.IllegalValue(_))    ⇒ true
      case _                                           ⇒ false
    }
  }

  test("CsvInput instances should have a working asCsvReader method") {
    forAll { data: List[CsvRow[Int]] ⇒
      assert(compare(toCsv(data).asCsvReader[List[Int]](',', false).toList, data))
    }
  }

  test("CsvInput instances should have a working readCsv method") {
    forAll { data: List[CsvRow[Int]] ⇒
      assert(compare(toCsv(data).readCsv[List, List[Int]](',', false), data))
    }
  }

  def compareUnsafe(csv: ⇒ List[List[Int]], data: List[CsvRow[Int]]): Boolean = {
    def cmp(csv: List[List[Int]], data: List[CsvRow[Int]]): Boolean = (csv, data) match {
      case (Nil, Nil) ⇒ true
      case (h1 :: t1, CodecValue.LegalValue(_, h2) :: t2) if h1 == h2 ⇒ cmp(t1, t2)
      case _ ⇒ false
    }

    Try(csv) match {
      case scala.util.Success(is) ⇒ cmp(is, data)
      case _                      ⇒ data.exists {
        case CodecValue.IllegalValue(_) ⇒ true
        case _                          ⇒ false
      }
    }
  }

  test("CsvInput instances should have a working asUnsafeCsvReader method") {
    forAll { data: List[CsvRow[Int]] ⇒
      assert(compareUnsafe(toCsv(data).asUnsafeCsvReader[List[Int]](',', false).toList, data))
    }
  }

  test("CsvInput instances should have a working unsafeReadCsv method") {
    forAll { data: List[CsvRow[Int]] ⇒
      assert(compareUnsafe(toCsv(data).unsafeReadCsv[List, List[Int]](',', false), data))
    }
  }
}
