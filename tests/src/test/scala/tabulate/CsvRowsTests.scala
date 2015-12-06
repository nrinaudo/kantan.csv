package tabulate

import org.scalacheck.Gen
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.CsvDataTests._
import tabulate.laws.discipline.arbitrary._
import tabulate.ops._

class CsvRowsTests extends FunSuite with GeneratorDrivenPropertyChecks {
  private def asCsvRows(csv: List[List[String]]): CsvRows[List[String]] =
    write(csv).asCsvRows[List[String]](',', false).map(_.get)

  test("empty.next should throw an exception") {
    intercept[NoSuchElementException] { CsvRows.empty.next() }
    ()
  }

  test("empty.hasNext should be false") {
    assert(!CsvRows.empty.hasNext)
  }

  test("empty.close should do nothing") {
    CsvRows.empty.close()
  }


  val csvAndIndex: Gen[(List[List[String]], Int)] = for {
    data  <- csv.suchThat(_.length > 1)
    index <- Gen.choose(1, data.length - 1)
  } yield (data, index)

  test("drop should behave as expected") {
    forAll(csvAndIndex) { case (csv, index) =>
      assert(asCsvRows(csv).drop(index).toList == csv.drop(index))
    }
  }

  test("take should behave as expected") {
    forAll(csvAndIndex) { case (csv, index) =>
      assert(asCsvRows(csv).take(index).toList == csv.take(index))
    }
  }

  def csvWith[A](ag: Gen[A]): Gen[List[List[String]]] = Gen.nonEmptyListOf(Gen.nonEmptyListOf(ag.map(_.toString)))
  val alphaCsv: Gen[List[List[String]]] = csvWith(Gen.nonEmptyListOf(Gen.alphaLowerChar).map(_.mkString))

  test("forall should return true when all entries match the predicate") {
    forAll(alphaCsv) { csv =>
      asCsvRows(csv).foreach { row =>
        assert(row.forall(!_.contains("1")))
      }

    }
  }

  test("forall should return false when at least one entry does not match the predicate") {
    forAll(alphaCsv) { csv =>
      asCsvRows(csv).foreach { row =>
        assert(!row.forall(_.forall(a => 97 > a || a > 122)))
      }
    }
  }

  test("find should find an element that matches the predicate") {
    forAll(alphaCsv) { csv =>
      asCsvRows(csv).foreach { row =>
        if(!row.find(s => s.exists(a => 97 <= a && a <= 122)).isDefined)
          println(row)
        assert(row.find(s => s.exists(a => 97 <= a && a <= 122)).isDefined)
      }
    }
  }

  test("find should not find anything when no element matches the predicate") {
    forAll(alphaCsv) { csv =>
      asCsvRows(csv).foreach { row =>
        if(!row.find(s => s.exists(a => 97 > a || a > 122)).isEmpty)
          println(row)
        assert(row.find(s => s.exists(a => 97 > a || a > 122)).isEmpty)
      }
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
