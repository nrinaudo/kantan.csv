package tabulate.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.CsvInput
import tabulate.ops._

/** Used to profile various aspects of tabulate. */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Profiling {
  @Benchmark
  def decode() = CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList

  @Benchmark
  def encode() = rawData.asCsvString(',')
}
