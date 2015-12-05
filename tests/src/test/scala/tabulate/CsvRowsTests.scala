package tabulate

import ops._

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.laws.discipline.arbitrary._
import CsvDataTests._

class CsvRowsTests extends FunSuite with GeneratorDrivenPropertyChecks {
  private def asCsvRows(csv: List[List[String]]): CsvRows[List[String]] =
    write(csv).asCsvRows[List[String]](',', false).map(_.get)

  test("CsvRows.empty.next should throw an exception") {
    intercept[NoSuchElementException] { CsvRows.empty.next() }
    ()
  }

  test("CsvRows.empty.hasNext should be false") {
    assert(!CsvRows.empty.hasNext)
  }

  test("CsvRows.empty.close should do nothing") {
    CsvRows.empty.close()
  }

  test("drop should behave as expected") {
    forAll(csv.suchThat(_.length > 1)) { csv =>
      assert(asCsvRows(csv).drop(1).toList == csv.drop(1))
    }
  }

  test("isTraversableAgain should return false") {
    forAll(csv) { csv =>
      assert(!asCsvRows(csv).isTraversableAgain)
    }
  }

  test("toStream should behave as expected") {
    forAll(csv) { csv =>
      assert(asCsvRows(csv).toStream.toList == csv)
    }
  }

  test("toTraversable should behave as expected") {
    forAll(csv) { csv =>
      assert(asCsvRows(csv).toTraversable.toList == csv)
    }
  }

  test("hasDefiniteSize should only return true for empty instances") {
    forAll(csv) { csv =>
      val rows = asCsvRows(csv)
      while(rows.hasNext) {
        assert(!rows.hasDefiniteSize)
        rows.next()
      }
      assert(rows.hasDefiniteSize)
    }
  }

  test("isEmpty should only return true for empty instances") {
    forAll(csv) { csv =>
      val rows = asCsvRows(csv)
      while(rows.hasNext) {
        assert(!rows.isEmpty)
        rows.next()
      }
      assert(rows.isEmpty)
    }
  }
}
