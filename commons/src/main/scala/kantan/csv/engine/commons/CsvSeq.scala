package kantan.csv.engine.commons

import org.apache.commons.csv.CSVRecord

private[commons] class CsvSeq(rec: CSVRecord) extends IndexedSeq[String] {
  override def length: Int = rec.size()
  override def apply(idx: Int): String = rec.get(idx)
}
