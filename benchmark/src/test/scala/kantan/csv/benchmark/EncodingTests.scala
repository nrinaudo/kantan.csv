package kantan.csv.benchmark

import kantan.csv.ops._
import org.scalatest.FunSuite

class EncodingTests extends FunSuite {
  val encoding = new Encoding

  def decode(str: String): List[CsvEntry] = str.unsafeReadCsv[List, CsvEntry](',', false)

  test("kantan internal") {
    assert(decode(encoding.kantanInternal) == rawData)
  }

  test("kantan jackson") {
    assert(decode(encoding.kantanJackson) == rawData)
  }

  test("kantan commons") {
    assert(decode(encoding.kantanCommons) == rawData)
  }

  test("kantan opencsv") {
    assert(decode(encoding.kantanOpenCsv) == rawData)
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
