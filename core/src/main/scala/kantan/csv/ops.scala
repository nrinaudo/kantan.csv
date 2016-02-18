package kantan.csv

import java.io.StringWriter

import kantan.csv.engine.WriterEngine

object ops extends CsvInput.ToCsvInputOps
                   with CsvOutput.ToCsvOutputOps
                   with RowEncoder.ToRowEncoderOps
                   with CellEncoder.ToCellEncoderOps {
  implicit class CellDecoderOps(val str: String) extends AnyVal {
    def fromCsvCell[A](implicit da: CellDecoder[A]): CsvResult[A] = da.decode(str)
    def unsafeFromCsvCell[A](implicit da: CellDecoder[A]): A = da.unsafeDecode(str)
  }

  implicit class RowDecoderOps(val row: Seq[String]) extends AnyVal {
    def fromCsvRow[A](implicit da: RowDecoder[A]): CsvResult[A] = da.decode(row)
    def unsafeFromCsvRow[A](implicit da: RowDecoder[A]): A = da.unsafeDecode(row)
  }

  implicit class TraversableOps[A: RowEncoder](val rows: TraversableOnce[A]) {
    def asCsv(sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): String = {
      val out = new StringWriter()
      CsvWriter(out, sep, header).write(rows).close()
      out.toString
    }
  }
}
