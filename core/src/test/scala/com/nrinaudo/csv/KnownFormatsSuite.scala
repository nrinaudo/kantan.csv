package com.nrinaudo.csv

import org.scalatest.FunSuite
import com.nrinaudo.csv.ops._

class KnownFormatsSuite extends FunSuite {
  implicit val carFormat = RowCodec.caseCodec5(Car.apply, Car.unapply)(1, 2, 3, 4, 0)
  case class Car(make: String, model: String, description: Option[String], price: Int, year: Int)

  def read(res: String): List[Car] = getClass.getResourceAsStream(res).asUnsafeCsvRows[Car](',', true).toList

  test("All known formats must be supported") {
    val raw = read("/raw.csv")

    info("Excel for Mac")
    assert(read("/excel_mac_12_0.csv") == raw)

    info("Numbers")
    assert(read("/numbers_1_0_3.csv") == raw)

    info("Google Docs")
    assert(read("/google_docs.csv") == raw)
  }
}
