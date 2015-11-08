package tabulate.benchmark

import tabulate.ops._

class CellData {
  val ints: List[Int] = (0 to 1000).toList
  val encodedInts: List[String] = ints.map(_.asCsvCell)
}
