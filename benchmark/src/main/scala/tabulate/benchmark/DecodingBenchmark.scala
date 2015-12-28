package tabulate.benchmark

import java.io.StringReader
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.CsvInput
import tabulate.engine.jackson.JacksonCsv

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class DecodingBenchmark {
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
  @Benchmark
  def tabulateInternal() =
    CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList

  @Benchmark
  def tabulateJackson() = {
    import tabulate.engine.jackson._
    CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList
  }

  @Benchmark
  def tabulateOpencsv() = {
    import tabulate.engine.opencsv._
    CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList
  }

  @Benchmark
  def tabulateCommons() = {
    import tabulate.engine.commons._
    CsvInput.string.unsafeRows[CsvEntry](strData, ',', false).toList
  }


  // Note: we must call trim on the input since product-collections does not accept the last row ending with a line
  // break. I believe that to be a bug.
  @Benchmark
  def productCollections() =
    com.github.marklister.collections.io.CsvParser[Int, String, Boolean, Float].iterator(new StringReader(strData.trim)).toList

  @Benchmark
  def opencsv() =
    new CsvIterator(new com.opencsv.CSVReader(new StringReader(strData)))(_.readNext()).toList

  @Benchmark
  def commons() = {
    val csv = org.apache.commons.csv.CSVFormat.RFC4180.parse(new StringReader(strData)).iterator()
    new Iterator[CsvEntry] {
      override def hasNext = csv.hasNext
      override def next() = {
        val n = csv.next()
        (n.get(0).toInt, n.get(1), n.get(2).toBoolean, n.get(3).toFloat)
      }
    }.toList
  }

  @Benchmark
  def jackson() =
    new CsvIterator(JacksonCsv.parse(new StringReader(strData), ','))(it =>
      if(it.hasNext) it.next()
      else           null
    ).toList

  @Benchmark
  def univocity() = {
    import com.univocity.parsers.csv._
    val parser = new CsvParser(new CsvParserSettings)
    parser.beginParsing(new StringReader(strData))

    new CsvIterator(parser)(_.parseNext()).toList
  }
}
