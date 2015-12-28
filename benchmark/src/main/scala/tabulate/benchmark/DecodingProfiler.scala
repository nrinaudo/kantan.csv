package tabulate.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.CsvInput
import tabulate.engine.ReaderEngine

/** Used to profile various aspects of tabulate. */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class DecodingProfiler {
  def decode(implicit engine: ReaderEngine) = CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList

  @Benchmark
  def internal() = decode

  @Benchmark
  def jackson() = decode(tabulate.engine.jackson.engine)

  @Benchmark
  def commons() = decode(tabulate.engine.commons.engine)

  @Benchmark
  def openCSv() = decode(tabulate.engine.opencsv.engine)
}
