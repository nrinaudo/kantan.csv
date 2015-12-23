package tabulate.benchmark

import java.io.StringWriter
import java.util.concurrent.TimeUnit

import org.apache.commons.csv.CSVFormat
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

  @Benchmark
  def commonsCsv() = {
    val out = new StringWriter()
    val writer = new org.apache.commons.csv.CSVPrinter(out, CSVFormat.RFC4180)
    write { a => writer.printRecords(a) }
    writer.close()
    out.close()
  }

  @Benchmark
  def jacksonCsv() = {
    val out = new StringWriter()
    val writer = JacksonCsv.write(out)
    write { a =>
      writer.write(a)
      ()
    }
    writer.close()
    out.close()
  }
}
