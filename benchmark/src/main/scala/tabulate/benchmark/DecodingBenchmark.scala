package tabulate.benchmark

import java.io.{StringReader, StringWriter}
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import tabulate.ops._

// benchmark/jmh:run -i 5 -wi 1 -f 1 -t 1 -rf csv -prof stack:detailLine=true;lines=10;period=5 tabulate.benchmark.DecodingBenchmark
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class DecodingBenchmark {
  type Input =  (Int, String, Boolean, Float)

  val input: List[Input] = List.tabulate(1000)(i =>
    (i, i.toChar.toString + i, i % 2 == 0, i / 100F)
  )

  val inputAsCsv: String = {
    val out = new StringWriter()
    input.foldLeft(out.asCsvWriter[Input](','))((out, a) => out.write(a)).close()
    out.toString
  }



  // - Helpers ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  class CsvIterator[A](iterator: A)(f: A => Array[String]) extends Iterator[Input] {
      var n = f(iterator)
      override def hasNext: Boolean = n != null
      override def next(): Input = {
        val temp = n
        n = f(iterator)
        toTuple(temp)
      }
    }

  def toTuple(row: Array[String]): Input = (row(0).toInt, row(1), row(2).toBoolean, row(3).toFloat)


  // - Benchmarks ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /* TODO:
  Jackson CSV parser
  SuperCSV
  JCSV Parser
  SimpleFlatMapper CSV parser
  Data pipeline
  Apache Commons CSV
  Simple CSV parser
  Java CSV Parser
  Oster Miller CSV parser
  Esperio CSV parser
  Way IO Parser
  Gen-Java CSV
  Bean IO Parser
   */

  @Benchmark
  def tabulate: List[Input] = inputAsCsv.asUnsafeCsvRows[Input](',', false).toList

  // Note: we must call trim on the input since product-collections does not accept the last row ending with a line
  // break. I believe that to be a bug.
  @Benchmark
  def productCollections: List[Input] =
    com.github.marklister.collections.io.CsvParser[Int, String, Boolean, Float].iterator(new StringReader(inputAsCsv.trim)).toList

  @Benchmark
  def opencsv: List[Input] =
    new CsvIterator(new com.opencsv.CSVReader(new StringReader(inputAsCsv)))(_.readNext()).toList

  @Benchmark
  def univocity: List[Input] = {
    import com.univocity.parsers.csv._
    val parser = new CsvParser(new CsvParserSettings)
    parser.beginParsing(new StringReader(inputAsCsv))

    new CsvIterator(parser)(_.parseNext()).toList
  }
}
