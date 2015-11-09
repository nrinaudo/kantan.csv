package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.RowDecoder

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class RowDecoderBenchmark extends BenchData {
  @Benchmark
  def decodeTuples = encodedTuples.map(t => RowDecoder[(Int, Boolean)].decode(t))
}
