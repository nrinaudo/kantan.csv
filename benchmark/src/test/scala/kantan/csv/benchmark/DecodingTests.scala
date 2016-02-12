package kantan.csv.benchmark

import org.scalatest.FunSuite

class DecodingTests extends FunSuite {
  val decoding = new Decoding

  test("kantan internal") {
    assert(decoding.kantanInternal == rawData)
  }

  test("kantan jackson") {
    assert(decoding.kantanJackson == rawData)
  }

  test("kantan commons") {
    assert(decoding.kantanCommons == rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("kantan opencsv") {
    assert(decoding.kantanOpenCsv == rawData)
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
