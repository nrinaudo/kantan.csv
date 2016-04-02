package kantan.csv.laws

import kantan.csv.RowCodec
import kantan.csv.engine.ReaderEngine
import kantan.csv.laws.KnownFormatsReaderLaws.Car
import kantan.csv.ops._

trait KnownFormatsReaderLaws {
  implicit def engine: ReaderEngine

  implicit val carFormat = RowCodec.caseCodec5(Car.apply)(Car.unapply)(1, 2, 3, 4, 0)

  def read(res: String): List[Car] = getClass.getResource(s"/known_formats/$res.csv").unsafeReadCsv(',', true)

  lazy val reference: List[Car] = read("raw")

  def excelMac12_0: Boolean = read("excel_mac_12_0") == reference

  def numbers1_0_3: Boolean = read("numbers_1_0_3") == reference

  def googleDocs: Boolean = read("google_docs") == reference
}

object KnownFormatsReaderLaws {
  case class Car(make: String, model: String, description: Option[String], price: Int, year: Int)
}
