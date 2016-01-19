package tabulate.benchmark

import java.io.StringWriter
import java.util.concurrent.TimeUnit

import org.apache.commons.csv.CSVFormat
import org.openjdk.jmh.annotations._
import tabulate.{RowEncoder, RowDecoder}
import tabulate.engine.WriterEngine
import tabulate.engine.jackson.JacksonCsv
import tabulate.ops._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Encoding {
  @Benchmark
  def tabulateInternal = Encoding.tabulate(rawData)

  @Benchmark
  def tabulateJackson = Encoding.tabulate(rawData)(tabulate.engine.jackson.engine)

  @Benchmark
  def tabulateOpencsv = Encoding.tabulate(rawData)(tabulate.engine.opencsv.engine)

  @Benchmark
  def tabulateCommons = Encoding.tabulate(rawData)(tabulate.engine.commons.engine)

  @Benchmark
  def productCollections = Encoding.productCollections(rawData)

  @Benchmark
  def opencsv = Encoding.opencsv(rawData)

  @Benchmark
  def commons = Encoding.commons(rawData)

  @Benchmark
  def jackson = Encoding.jackson(rawData)

  @Benchmark
  def univocity = Encoding.univocity(rawData)

  @Benchmark
  def scalaCsv = Encoding.scalaCsv(rawData)
}

object Encoding {
  def write[A](data: List[CsvEntry])(f: Array[String] => Unit): Unit =
    data.foreach { entry => f(Array(entry._1.toString, entry._2.toString, entry._3.toString, entry._4.toString)) }

  def tabulate(data: List[CsvEntry])(implicit engine: WriterEngine) = data.asCsv(',')

  def productCollections(data: List[CsvEntry]) = {
    val out = new StringWriter()
    com.github.marklister.collections.io.Utils.CsvOutput(data).writeCsv(out, ",")
    out.close()
    out.toString
  }

  def opencsv(data: List[CsvEntry]) = {
    val out = new StringWriter()
    val writer = new com.opencsv.CSVWriter(out, ',')
    write(data) { a => writer.writeNext(a) }
    writer.close()
    out.close()
    out.toString
  }

  def commons(data: List[CsvEntry]) = {
    val out = new StringWriter()
    val writer = new org.apache.commons.csv.CSVPrinter(out, CSVFormat.RFC4180)
    write(data) { a => writer.printRecords(a) }
    writer.close()
    out.close()
    out.toString
  }

  def jackson(data: List[CsvEntry]) = {
    val out = new StringWriter()
    val writer = JacksonCsv.write(out, ',')
    write(data) { a =>
      writer.write(a)
      ()
    }
    writer.close()
    out.close()
    out.toString
  }

  def univocity(data: List[CsvEntry]) = {
    import com.univocity.parsers.csv._

    val out = new StringWriter()
    val writer = new CsvWriter(out, new CsvWriterSettings())
    write(data) { a =>
      writer.writeRow(a:_*)
    }
    writer.close()
    out.close()
    out.toString
  }

  def scalaCsv(data: List[CsvEntry]) = {
    import com.github.tototoshi.csv._
    val encoder = implicitly[RowEncoder[CsvEntry]]

    val out = new StringWriter()
    val writer = CSVWriter.open(out)
    writer.writeAll(data.map(encoder.encode))
    writer.close()
    out.close()
    out.toString
  }
}
