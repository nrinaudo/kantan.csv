package tabulate.benchmark

import java.io.StringWriter
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class EncodingBenchmark {
  @Benchmark
  def tabulate() = new StringWriter().writeCsv(rawData, ',').toString

  @Benchmark
  def productCollections() = {
    val out = new StringWriter()
    com.github.marklister.collections.io.Utils.CsvOutput(rawData).writeCsv(out)
    out.toString
  }
}
