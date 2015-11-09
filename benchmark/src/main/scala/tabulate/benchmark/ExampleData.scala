package tabulate.benchmark

import java.io.StringWriter

import tabulate._
import tabulate.ops._

class ExampleData {
  type Tuple = (Int, Boolean)

  // - Helper methods --------------------------------------------------------------------------------------------------
  // ------------------------------------------------------------------------------------------------------------------
  @inline def print[A: RowEncoder](as: List[A]): String = {
    val out = new StringWriter()
    as.foldLeft(out.asCsvWriter[A](','))(_ write _)
    out.toString
  }

  @inline def parse[A: RowDecoder](s: String): List[DecodeResult[A]] = s.asCsvRows[A](',', false).toList

  @inline def encodeCell[A: CellEncoder](as: List[A]): List[String] = as.map(_.asCsvCell)
  @inline def encodeRow[A: RowEncoder](as: List[A]): List[Seq[String]] = as.map(_.asCsvRow)
  @inline def decodeCell[A](ss: List[String])(implicit d: CellDecoder[A]): List[DecodeResult[A]] = ss.map(d.decode)
  @inline def decodeRow[A](ss: List[Seq[String]])(implicit d: RowDecoder[A]): List[DecodeResult[A]] = ss.map(d.decode)



  val ints: List[Int] = (0 to 1000).toList
  val intCells: List[String] = encodeCell(ints)

  val options: List[Option[Int]] = ints.map { i =>
    if(i % 2 == 0) Some(i)
    else           None
  }
  val optionCells: List[String] = encodeCell(options)

  val tuples: List[Tuple] = ints.zip(ints.map(_ % 2 == 0))
  val tupleRows: List[Seq[String]] = encodeRow(tuples)
  val tupleCsv: String = print(tuples)
}
