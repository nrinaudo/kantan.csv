package tabulate.benchmark

import java.io.StringReader
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.{RowDecoder, CsvInput}
import tabulate.engine.ReaderEngine
import tabulate.engine.jackson.JacksonCsv

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Decoding {
  @Benchmark
  def tabulateInternal = Decoding.tabulate(strData)

  @Benchmark
  def tabulateJackson = Decoding.tabulate(strData)(tabulate.engine.jackson.engine)

  @Benchmark
  def tabulateOpencsv = Decoding.tabulate(strData)(tabulate.engine.opencsv.engine)

  @Benchmark
  def tabulateCommons = Decoding.tabulate(strData)(tabulate.engine.commons.engine)

  @Benchmark
  def productCollections = Decoding.productCollections(strData)

  @Benchmark
  def opencsv = Decoding.opencsv(strData)

  @Benchmark
  def commons = Decoding.commons(strData)

  @Benchmark
  def jackson = Decoding.jackson(strData)

  @Benchmark
  def univocity = Decoding.univocity(strData)

  @Benchmark
  def scalaCsv = Decoding.scalaCsv(strData)
}


object Decoding {
  // - Helpers ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  class CsvIterator[A](iterator: A)(f: A => Array[String]) extends Iterator[CsvEntry] {
    var n = f(iterator)
    override def hasNext: Boolean = n != null
    override def next(): CsvEntry = {
      val temp = n
      n = f(iterator)
      toTuple(temp)
    }
  }

  def toTuple(row: Array[String]): CsvEntry = (row(0).toInt, row(1), row(2).toBoolean, row(3).toFloat)


  // - Benchmarks ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def tabulate(str: String)(implicit engine: ReaderEngine) = CsvInput.string.unsafeReader[CsvEntry](str, ',', false).toList



  // Note: we must call trim on the input since product-collections does not accept the last row ending with a line
  // break. I believe that to be a bug.
  def productCollections(str: String) =
    com.github.marklister.collections.io.CsvParser[Int, String, Boolean, Float].iterator(new StringReader(str.trim)).toList

  def opencsv(str: String) =
    new CsvIterator(new com.opencsv.CSVReader(new StringReader(str)))(_.readNext()).toList

  def commons(str: String) = {
    val csv = org.apache.commons.csv.CSVFormat.RFC4180.parse(new StringReader(str)).iterator()
    new Iterator[CsvEntry] {
      override def hasNext = csv.hasNext
      override def next() = {
        val n = csv.next()
        (n.get(0).toInt, n.get(1), n.get(2).toBoolean, n.get(3).toFloat)
      }
    }.toList
  }

  def jackson(str: String) =
    new CsvIterator(JacksonCsv.parse(new StringReader(str), ','))(it =>
      if(it.hasNext) it.next()
      else           null
    ).toList

  val univocitySettings = {
    val settings = new com.univocity.parsers.csv.CsvParserSettings
    settings.setReadInputOnSeparateThread(false)
    settings.setInputBufferSize(2048)
    settings.setIgnoreLeadingWhitespaces(true)
    settings.setIgnoreLeadingWhitespaces(false)
    settings
  }

  def univocity(str: String) = {
    val parser = new com.univocity.parsers.csv.CsvParser(univocitySettings)
    parser.beginParsing(new StringReader(str))
    new CsvIterator(parser)(_.parseNext()).toList
  }

  def scalaCsv(str: String) = {
    import com.github.tototoshi.csv._
    val decoder = implicitly[RowDecoder[CsvEntry]]
    CSVReader.open(new StringReader(str)).iterator.map(r => decoder.decode(r).get).toList
  }
}
