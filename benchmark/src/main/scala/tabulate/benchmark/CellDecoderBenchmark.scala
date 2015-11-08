package tabulate.benchmark

import org.openjdk.jmh.annotations._
import tabulate.{DecodeResult, CellDecoder}

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CellDecoderBenchmark extends CellData {
  @Benchmark
  def decodeInt: List[DecodeResult[Int]] = encodedInts.map(s => CellDecoder[Int].decode(s))
}
