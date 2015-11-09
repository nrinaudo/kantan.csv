package tabulate.benchmark

import tabulate.ops._

class BenchData {
  val ints: List[Int] = (0 to 1000).toList
  val encodedInts: List[String] = ints.map(_.asCsvCell)

  val options: List[Option[Int]] = ints.map { i =>
    if(i % 2 == 0) Some(i)
    else           None
  }
  val encodedOptions: List[String] = options.map(_.asCsvCell)

  val tuples: List[(Int, Boolean)] = ints.zip(ints.map(_ % 2 == 0))
  val encodedTuples: List[Seq[String]] = tuples.map(_.asCsvRow)
}
