package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class RowEncoderBenchmark extends BenchData {
  @Benchmark
  def encodeTuples = tuples.map(_.asCsvRow)
}
