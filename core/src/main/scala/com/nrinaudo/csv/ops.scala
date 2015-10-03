package com.nrinaudo.csv

object ops extends CsvInput.ToCsvInputOps
                   with CsvOutput.ToCsvOutputOps
                   with RowEncoder.ToRowEncoderOps
                   with CellEncoder.ToCellEncoderOps
