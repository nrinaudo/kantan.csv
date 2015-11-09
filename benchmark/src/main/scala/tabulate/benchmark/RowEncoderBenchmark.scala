package tabulate.benchmark

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class RowEncoderBenchmark extends ExampleData {
  @Benchmark
  def encodeTuples = encodeRow(tuples)
}
