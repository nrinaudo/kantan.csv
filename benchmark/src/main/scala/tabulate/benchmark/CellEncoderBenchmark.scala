package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CellEncoderBenchmark extends ExampleData {
  @Benchmark
  def encodeInt = encodeCell(ints)

  @Benchmark
  def encodeOptions = encodeCell(options)
}
