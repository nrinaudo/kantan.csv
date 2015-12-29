package tabulate

import org.scalacheck.Gen
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.ops._

class CsvWriterTests extends FunSuite with GeneratorDrivenPropertyChecks {
  def csvWith[A](ag: Gen[A]): Gen[List[List[String]]] = Gen.nonEmptyListOf(Gen.nonEmptyListOf(ag.map(_.toString)))
  val alphaNumCsv: Gen[List[List[String]]] = csvWith(Gen.identifier)
  val lineBreakCsv: Gen[List[List[String]]] = csvWith(Gen.listOf(Gen.oneOf(Gen.choose(97.toChar, 122.toChar), Gen.oneOf('\r', '\n'))))
  val doubleQuoteCsv: Gen[List[List[String]]] = csvWith(Gen.listOf(Gen.oneOf(Gen.choose(97.toChar, 122.toChar), Gen.const('"'))))
  val separatorCsv: Gen[List[List[String]]] = csvWith(Gen.listOf(Gen.oneOf(Gen.choose(97.toChar, 122.toChar), Gen.const(','))))

  test("writing should behave properly on alpha-numeric strings") {
    forAll(alphaNumCsv) { csv => assert(csv.asCsvString(',').unsafeReadCsv[List[String], List](',', false) == csv) }
  }

  test("writing should behave properly on strings with line breaks") {
    forAll(lineBreakCsv) { csv => assert(csv.asCsvString(',').unsafeReadCsv[List[String], List](',', false) == csv) }
  }

  test("writing should behave properly on strings with separators") {
    forAll(separatorCsv) { csv => assert(csv.asCsvString(',').unsafeReadCsv[List[String], List](',', false) == csv) }
  }

  test("writing should behave properly on strings with double quotes") {
    forAll(doubleQuoteCsv) { csv =>
      assert(csv.asCsvString(',').unsafeReadCsv[List[String], List](',', false) == csv)
    }
  }
}
