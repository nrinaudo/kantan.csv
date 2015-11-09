package tabulate.benchmark

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CellDecoderBenchmark extends ExampleData {
  @Benchmark
  def decodeInt = decodeCell[Int](intCells)

  @Benchmark
  def decodeOptions = decodeCell[Option[Int]](optionCells)
}
