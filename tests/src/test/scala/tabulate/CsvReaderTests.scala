package tabulate

import org.scalacheck.Gen
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.laws.discipline.arbitrary._
import tabulate.ops._

class CsvReaderTests extends FunSuite with GeneratorDrivenPropertyChecks {
  private def asCsvRows(csv: List[List[String]]): CsvReader[List[String]] =
    csv.asCsv(',').asUnsafeCsvReader[List[String]](',', false)

  val csvAndRange: Gen[(List[List[String]], Int, Int)] = for {
    data  <- csv.suchThat(_.length > 1)
    index <- Gen.choose(0, data.length - 1)
    length <- Gen.choose(1, data.length - index)
  } yield (data, index, length)
  test("copyToArray should behave as expected") {
    forAll(csvAndRange) { case (csv, from, count) =>
      val a1, a2 = new Array[List[String]](count)
      asCsvRows(csv).copyToArray(a1, from, count)
      csv.copyToArray(a2, from, count)
      assert(a1.sameElements(a2))
    }
  }
}
