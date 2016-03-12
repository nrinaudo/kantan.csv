package kantan.csv

import java.io.StringWriter

import kantan.csv.engine.{ReaderEngine, WriterEngine}

import scala.collection.generic.CanBuildFrom

object ops {
  implicit class CsvOutputOps[A](val a: A) extends AnyVal {
    def asCsvWriter[B: RowEncoder](sep: Char, header: Seq[String] = Seq.empty)(implicit oa: CsvOutput[A], e: WriterEngine): CsvWriter[B] =
      oa.writer(a, sep, header)

    def writeCsv[B: RowEncoder](rows: TraversableOnce[B], sep: Char, header: Seq[String] = Seq.empty)(implicit oa: CsvOutput[A], e: WriterEngine): Unit =
      oa.write(a, rows, sep, header)
  }

  implicit class CsvInputOps[A](val a: A) extends AnyVal {
    /** Shorthand for [[CsvInput!.reader CsvInput.reader]]. */
    def asCsvReader[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], e: ReaderEngine): CsvReader[CsvResult[B]] =
      ai.reader[B](a, sep, header)

    /** Shorthand for [[CsvInput.unsafeReader]]. */
    def asUnsafeCsvReader[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], e: ReaderEngine): CsvReader[B] =
      ai.unsafeReader[B](a, sep, header)

    /** Shorthand for [[CsvInput.read]]. */
    def readCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], cbf: CanBuildFrom[Nothing, CsvResult[B], C[CsvResult[B]]], e: ReaderEngine) =
      ai.read[C, B](a, sep, header)

    /** Shorthand for [[CsvInput.unsafeRead]]. */
    def unsafeReadCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], cbf: CanBuildFrom[Nothing, B, C[B]], e: ReaderEngine) =
      ai.unsafeRead[C, B](a, sep, header)
  }

  implicit class CellDecoderOps(val str: String) extends AnyVal {
    def fromCsvCell[A](implicit da: CellDecoder[A]): CsvResult[A] = da.decode(str)
    def unsafeFromCsvCell[A](implicit da: CellDecoder[A]): A = da.unsafeDecode(str)
  }

  implicit class RowDecoderOps(val row: Seq[String]) extends AnyVal {
    def fromCsvRow[A](implicit da: RowDecoder[A]): CsvResult[A] = da.decode(row)
    def unsafeFromCsvRow[A](implicit da: RowDecoder[A]): A = da.unsafeDecode(row)
  }

  implicit class TraversableOnceOps[A](val rows: TraversableOnce[A]) extends AnyVal {
    def asCsv(sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine, ae: RowEncoder[A]): String = {
      val out = new StringWriter()
      CsvWriter(out, sep, header).write(rows).close()
      out.toString
    }
  }
}
