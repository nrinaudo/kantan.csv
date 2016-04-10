/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  def kantanInternal: List[CsvEntry] = Decoding.kantan(strData)

  @Benchmark
  def kantanJackson: List[CsvEntry] = Decoding.kantan(strData)(kantan.csv.engine.jackson.reader)

  @Benchmark
  def kantanOpenCsv: List[CsvEntry] = Decoding.kantan(strData)(kantan.csv.engine.opencsv.reader)

  @Benchmark
  def kantanCommons: List[CsvEntry] = Decoding.kantan(strData)(kantan.csv.engine.commons.reader)

  @Benchmark
  def productCollections: List[CsvEntry] = Decoding.productCollections(strData)

  @Benchmark
  def opencsv: List[CsvEntry] = Decoding.opencsv(strData)

  @Benchmark
  def commons: List[CsvEntry] = Decoding.commons(strData)

  @Benchmark
  def jackson: List[CsvEntry] = Decoding.jackson(strData)

  @Benchmark
  def univocity: List[CsvEntry] = Decoding.univocity(strData)

  @Benchmark
  def scalaCsv: List[CsvEntry] = Decoding.scalaCsv(strData)
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
  def kantan(str: String)(implicit e: ReaderEngine): List[CsvEntry] =
    CsvInput.string.unsafeReader[CsvEntry](str, ',', false).toList



  // Note: we must call trim on the input since product-collections does not accept the last row ending with a line
  // break. I believe that to be a bug.
  def productCollections(str: String): List[CsvEntry] =
    com.github.marklister.collections.io.CsvParser[Int, String, Boolean, Float]
      .iterator(new StringReader(str.trim))
      .toList

  def opencsv(str: String): List[CsvEntry] =
    new CsvIterator(new com.opencsv.CSVReader(new StringReader(str)))(_.readNext()).toList

  def commons(str: String): List[CsvEntry] = {
    val csv = org.apache.commons.csv.CSVFormat.RFC4180.parse(new StringReader(str)).iterator()
    new Iterator[CsvEntry] {
      override def hasNext = csv.hasNext
      override def next() = {
        val n = csv.next()
        (n.get(0).toInt, n.get(1), n.get(2).toBoolean, n.get(3).toFloat)
      }
    }.toList
  }

  def jackson(str: String): List[CsvEntry] =
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

  def univocity(str: String): List[CsvEntry] = {
    val parser = new com.univocity.parsers.csv.CsvParser(univocitySettings)
    parser.beginParsing(new StringReader(str))
    new CsvIterator(parser)(_.parseNext()).toList
  }

  def scalaCsv(str: String): List[CsvEntry] = {
    import com.github.tototoshi.csv._
    CSVReader.open(new StringReader(str)).iterator.map(r ⇒ (r(0).toInt, r(1), r(2).toBoolean, r(3).toFloat)).toList
  }
}
