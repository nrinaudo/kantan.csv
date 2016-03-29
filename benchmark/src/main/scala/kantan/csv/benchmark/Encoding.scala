package kantan.csv.benchmark

import java.io.StringWriter
import java.util.concurrent.TimeUnit
import kantan.csv.engine.WriterEngine
import kantan.csv.engine.jackson.JacksonCsv
import kantan.csv.ops._
import org.apache.commons.csv.CSVFormat
import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Encoding {
  @Benchmark
  def kantanInternal: String = Encoding.kantan(rawData)

  @Benchmark
  def kantanJackson: String = Encoding.kantan(rawData)(kantan.csv.engine.jackson.writer)

  @Benchmark
  def kantanOpenCsv: String = Encoding.kantan(rawData)(kantan.csv.engine.opencsv.writer)

  @Benchmark
  def kantanCommons: String = Encoding.kantan(rawData)(kantan.csv.engine.commons.writer)

  @Benchmark
  def productCollections: String = Encoding.productCollections(rawData)

  @Benchmark
  def opencsv: String = Encoding.opencsv(rawData)

  @Benchmark
  def commons: String = Encoding.commons(rawData)

  @Benchmark
  def jackson: String = Encoding.jackson(rawData)

  @Benchmark
  def univocity: String = Encoding.univocity(rawData)

  @Benchmark
  def scalaCsv: String = Encoding.scalaCsv(rawData)
}

object Encoding {
  def write[A](data: List[CsvEntry])(f: Array[String] ⇒ Unit): Unit =
    data.foreach { entry ⇒ f(Array(entry._1.toString, entry._2.toString, entry._3.toString, entry._4.toString)) }

  def kantan(data: List[CsvEntry])(implicit engine: WriterEngine): String = data.asCsv(',')

  def productCollections(data: List[CsvEntry]): String = {
    val out = new StringWriter()
    com.github.marklister.collections.io.Utils.CsvOutput(data).writeCsv(out, ",")
    out.close()
    out.toString
  }

  def opencsv(data: List[CsvEntry]): String = {
    val out = new StringWriter()
    val writer = new com.opencsv.CSVWriter(out, ',')
    write(data) { a ⇒ writer.writeNext(a) }
    writer.close()
    out.close()
    out.toString
  }

  def commons(data: List[CsvEntry]): String = {
    val out = new StringWriter()
    val writer = new org.apache.commons.csv.CSVPrinter(out, CSVFormat.RFC4180)
    write(data) { a ⇒ writer.printRecords(a) }
    writer.close()
    out.close()
    out.toString
  }

  def jackson(data: List[CsvEntry]): String = {
    val out = new StringWriter()
    val writer = JacksonCsv.write(out, ',')
    write(data) { a ⇒
      writer.write(a)
      ()
    }
    writer.close()
    out.close()
    out.toString
  }

  def univocity(data: List[CsvEntry]): String = {
    import com.univocity.parsers.csv._

    val out = new StringWriter()
    val writer = new CsvWriter(out, new CsvWriterSettings())
    write(data) { a ⇒
      writer.writeRow(a:_*)
    }
    writer.close()
    out.close()
    out.toString
  }

  def scalaCsv(data: List[CsvEntry]): String = {
    import com.github.tototoshi.csv._

    val out = new StringWriter()
    val writer = CSVWriter.open(out)
    data.foreach { row ⇒
      writer.writeRow(List(row._1.toString, row._2.toString, row._3.toString, row._4.toString))
    }
    writer.close()
    out.close()
    out.toString
  }
}
