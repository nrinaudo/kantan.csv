package tabulate.benchmark

import java.io.{StringWriter, Writer}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class EncodingBenchmark {
  def write[A](f: Array[String] => Unit): Unit =
    rawData.foreach { entry => f(Array(entry._1.toString, entry._2.toString, entry._3.toString, entry._4.toString)) }

  @Benchmark
  def tabulate() = new StringWriter().writeCsv(rawData, ',').close()

  @Benchmark
  def productCollections() = {
    val out = new StringWriter()
    com.github.marklister.collections.io.Utils.CsvOutput(rawData).writeCsv(out, ",")
    out.close()
  }

  @Benchmark
  def opencsv() = {
    val out = new StringWriter()
    val writer = new com.opencsv.CSVWriter(out, ',')
    write { a => writer.writeNext(a) }
    writer.close()
    out.close()
  }
}
