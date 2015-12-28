package tabulate.benchmark

import java.io.StringWriter
import java.util.concurrent.TimeUnit

import org.apache.commons.csv.CSVFormat
import org.openjdk.jmh.annotations._
import tabulate.engine.WriterEngine
import tabulate.engine.jackson.JacksonCsv
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Encoding {
  def write[A](f: Array[String] => Unit): Unit =
    rawData.foreach { entry => f(Array(entry._1.toString, entry._2.toString, entry._3.toString, entry._4.toString)) }

  def tabulateEngine(implicit engine: WriterEngine) = rawData.asCsvString(',')

  @Benchmark
  def tabulateInternal() = tabulateEngine

  @Benchmark
  def tabulateJackson() = tabulateEngine(tabulate.engine.jackson.engine)

  @Benchmark
  def tabulateOpencsv() = tabulateEngine(tabulate.engine.opencsv.engine)

  @Benchmark
  def tabulateCommons() = tabulateEngine(tabulate.engine.commons.engine)

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
  def commons() = {
    val out = new StringWriter()
    val writer = new org.apache.commons.csv.CSVPrinter(out, CSVFormat.RFC4180)
    write { a => writer.printRecords(a) }
    writer.close()
    out.close()
  }

  @Benchmark
  def jackson() = {
    val out = new StringWriter()
    val writer = JacksonCsv.write(out, ',')
    write { a =>
      writer.write(a)
      ()
    }
    writer.close()
    out.close()
  }

  @Benchmark
  def univocity() = {
    import com.univocity.parsers.csv._

    val out = new StringWriter()
    val writer = new CsvWriter(out, new CsvWriterSettings())
    write { a =>
      writer.writeRow(a:_*)
    }
    writer.close()
    out.close()
  }
}
