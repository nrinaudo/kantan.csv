package tabulate

import org.scalatest.FunSuite

import ops._

class KnownFormatsTests extends FunSuite {
  implicit val carFormat = RowCodec.caseCodec5(Car.apply, Car.unapply)(1, 2, 3, 4, 0)
  case class Car(make: String, model: String, description: Option[String], price: Int, year: Int)

  def read(res: String): List[Car] = getClass.getResource(res).unsafeReadCsv(',', true)

  test("All known formats must be supported") {
    val raw = read("/known/raw.csv")

    info("Excel for Mac")
    assert(read("/known/excel_mac_12_0.csv") == raw)

    info("Numbers")
    assert(read("/known/numbers_1_0_3.csv") == raw)

    info("Google Docs")
    assert(read("/known/google_docs.csv") == raw)
  }
}
