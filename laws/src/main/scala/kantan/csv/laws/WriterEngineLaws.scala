package kantan.csv.laws

import kantan.csv.engine.WriterEngine
import kantan.csv.ops._

trait WriterEngineLaws {
  implicit def engine: WriterEngine

  def roundTrip(csv: List[List[Cell]], header: Seq[String]): Boolean =
    csv.asCsv(',', header).unsafeReadCsv[List, List[Cell]](',', header.nonEmpty) == csv

  def noTrailingSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').split("\n").forall(!_.endsWith(","))

  // This test is slightly dodgy, but works: we're assuming that the data is properly serialized (this is checked by
  // roundTrip), an want to make sure that we get the right number of rows. The `trim` bit is to allow for the optional
  // empty row.
  def crlfAsRowSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').trim.split("\r\n").length == csv.length
}

object WriterEngineLaws {
  def apply(e: WriterEngine): WriterEngineLaws = new WriterEngineLaws {
    override implicit val engine = e
  }
}