package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CellEncoderBenchmark extends CellData {
  @Benchmark
  def encodeInt = ints.map(_.asCsvCell)
}
