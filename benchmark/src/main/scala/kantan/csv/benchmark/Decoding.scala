package kantan.csv.benchmark

import java.io.StringReader
import java.util.concurrent.TimeUnit
import kantan.csv.CsvInput
import kantan.csv.engine.ReaderEngine
import kantan.csv.engine.jackson.JacksonCsv
import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Decoding {
  @Benchmark
  def kantanInternal = Decoding.kantan(strData)

  @Benchmark
  def kantanJackson = Decoding.kantan(strData)(kantan.csv.engine.jackson.engine)

  @Benchmark
  def kantanOpenCsv = Decoding.kantan(strData)(kantan.csv.engine.opencsv.engine)

  @Benchmark
  def kantanCommons = Decoding.kantan(strData)(kantan.csv.engine.commons.engine)

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
  class CsvIterator[A](iterator: A)(f: A ⇒ Array[String]) extends Iterator[CsvEntry] {
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
  def kantan(str: String)(implicit engine: ReaderEngine) = CsvInput.string.unsafeReader[CsvEntry](str, ',', false).toList



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
    new CsvIterator(JacksonCsv.parse(new StringReader(str), ','))(it ⇒
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
    CSVReader.open(new StringReader(str)).iterator.map(r ⇒ (r(0).toInt, r(1), r(2).toBoolean, r(3).toFloat)).toList
  }
}
