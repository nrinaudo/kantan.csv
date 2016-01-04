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

  // TODO: broken, need to investigate why
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

// TODO: broken, need to investigate why
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
}
