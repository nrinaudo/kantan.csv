package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.CellDecoder

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CellDecoderBenchmark extends BenchData {
  @Benchmark
  def decodeInt = encodedInts.map(s => CellDecoder[Int].decode(s))

  @Benchmark
  def decodeOptions = encodedOptions.map(s => CellDecoder[Option[Int]].decode(s))
}
