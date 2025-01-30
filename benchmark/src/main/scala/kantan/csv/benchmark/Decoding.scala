/*
 * Copyright 2015 Nicolas Rinaudo
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

import com.univocity.parsers.csv.CsvParserSettings
import java.io.StringReader
import java.util.concurrent.TimeUnit
import kantan.csv.{rfc, CsvSource}
import kantan.csv.engine.ReaderEngine
import kantan.csv.engine.jackson.defaultMappingIteratorBuilder
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit, Scope, State}

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Decoding {
  @Benchmark
  def kantanInternal: List[CsvEntry] = Decoding.kantan(strData)

  @Benchmark
  def kantanJackson: List[CsvEntry] = Decoding.kantan(strData)(kantan.csv.engine.jackson.jacksonCsvReaderEngine)

  @Benchmark
  def kantanCommons: List[CsvEntry] = Decoding.kantan(strData)(kantan.csv.engine.commons.commonsCsvReaderEngine)

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
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  class CsvIterator[A](iterator: A)(f: A => Array[String]) extends Iterator[CsvEntry] {
    private var n                 = f(iterator)
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
    CsvSource[String].unsafeReader[CsvEntry](str, rfc).toList

  def opencsv(str: String): List[CsvEntry] =
    new CsvIterator(new com.opencsv.CSVReader(new StringReader(str)))(_.readNext()).toList

  def commons(str: String): List[CsvEntry] = {
    val csv = org.apache.commons.csv.CSVFormat.RFC4180.parse(new StringReader(str)).iterator()
    new Iterator[CsvEntry] {
      override def hasNext: Boolean = csv.hasNext
      override def next(): CsvEntry = {
        val n = csv.next()
        (n.get(0).toInt, n.get(1), n.get(2).toBoolean, n.get(3).toFloat)
      }
    }.toList
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def jackson(str: String): List[CsvEntry] =
    new CsvIterator(defaultMappingIteratorBuilder(new StringReader(str), rfc))({ it =>
      if(it.hasNext) it.next()
      else null
    }).toList

  val univocitySettings: CsvParserSettings = {
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

  @SuppressWarnings(Array("org.wartremover.warts.SeqApply"))
  def scalaCsv(str: String): List[CsvEntry] = {
    import com.github.tototoshi.csv.CSVReader

    CSVReader.open(new StringReader(str)).iterator.map(r => (r(0).toInt, r(1), r(2).toBoolean, r(3).toFloat)).toList
  }
}
