package tabulate

object ops extends CsvInput.ToCsvInputOps
                   with CsvOutput.ToCsvOutputOps
                   with RowEncoder.ToRowEncoderOps
                   with CellEncoder.ToCellEncoderOps {
  implicit class CellDecoderOps(val str: String) extends AnyVal {
    def parseCsvCell[A: CellDecoder]: DecodeResult[A] = CellDecoder[A].decode(str)
  }

  implicit class RowDecoderOps(val row: Seq[String]) extends AnyVal {
    def parseCsvRow[A: RowDecoder]: DecodeResult[A] = RowDecoder[A].decode(row)
  }
}
