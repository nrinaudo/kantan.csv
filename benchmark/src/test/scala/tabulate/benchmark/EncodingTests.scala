package tabulate.benchmark

import org.scalatest.FunSuite
import tabulate.ops._

class EncodingTests extends FunSuite {
  val encoding = new Encoding

  def decode(str: String): List[CsvEntry] = str.unsafeReadCsv[List, CsvEntry](',', false)

  test("tabulate internal") {
    assert(decode(encoding.tabulateInternal) == rawData)
  }

  test("tabulate jackson") {
    assert(decode(encoding.tabulateJackson) == rawData)
  }

  test("tabulate commons") {
    assert(decode(encoding.tabulateCommons) == rawData)
  }

  test("tabulate opencsv") {
    assert(decode(encoding.tabulateOpencsv) == rawData)
  }

  test("jackson") {
    assert(decode(encoding.jackson) == rawData)
  }

  test("commons") {
    assert(decode(encoding.commons) == rawData)
  }

  test("opencsv") {
    assert(decode(encoding.opencsv) == rawData)
  }

  test("product-collections") {
    assert(decode(encoding.productCollections) == rawData)
  }

  test("univocity") {
    assert(decode(encoding.univocity) == rawData)
  }

  test("scala-csv") {
    assert(decode(encoding.scalaCsv) == rawData)
  }
}
