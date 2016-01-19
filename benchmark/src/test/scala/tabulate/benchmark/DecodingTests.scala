package tabulate.benchmark

import org.scalatest.FunSuite

class DecodingTests extends FunSuite {
  val decoding = new Decoding

  test("tabulate internal") {
    assert(decoding.tabulateInternal == rawData)
  }

  test("tabulate jackson") {
    assert(decoding.tabulateJackson == rawData)
  }

  test("tabulate commons") {
    assert(decoding.tabulateCommons == rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("tabulate opencsv") {
    assert(decoding.tabulateOpencsv == rawData)
  }
  */

  test("jackson") {
    assert(decoding.jackson == rawData)
  }

  test("commons") {
    assert(decoding.commons == rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("opencsv") {
    assert(decoding.opencsv == rawData)
  }
  */

  test("product-collections") {
    assert(decoding.productCollections == rawData)
  }

  test("univocity") {
    assert(decoding.univocity == rawData)
  }

  test("scala-csv") {
    assert(decoding.scalaCsv == rawData)
  }
}
