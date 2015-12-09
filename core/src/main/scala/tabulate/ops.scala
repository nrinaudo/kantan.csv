package tabulate

object ops extends CsvInput.ToCsvInputOps
                   with CsvOutput.ToCsvOutputOps
                   with RowEncoder.ToRowEncoderOps
                   with CellEncoder.ToCellEncoderOps {
  implicit class CellDecoderOps(val str: String) extends AnyVal {
    def parseCsvCell[A](implicit da: CellDecoder[A]): DecodeResult[A] = da.decode(str)
  }

  implicit class RowDecoderOps(val row: Seq[String]) extends AnyVal {
    def parseCsvRow[A](implicit da: RowDecoder[A]): DecodeResult[A] = da.decode(row)
  }
}
