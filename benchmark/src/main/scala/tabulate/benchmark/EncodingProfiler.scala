package tabulate.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.engine.WriterEngine
import tabulate.ops._

/** Used to profile various aspects of tabulate. */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class EncodingProfiler {
  def encode(implicit engine: WriterEngine) = rawData.asCsvString(',')(engine)

  @Benchmark
  def internal() = encode

  @Benchmark
  def tabulateJackson() = encode(tabulate.engine.jackson.engine)

  @Benchmark
  def tabulateOpencsv() = encode(tabulate.engine.opencsv.engine)

  @Benchmark
  def tabulateCommons() = encode(tabulate.engine.commons.engine)
}
