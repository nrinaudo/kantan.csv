/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.csv.engine

import com.fasterxml.jackson.databind.{MappingIterator, SequenceWriter}
import com.fasterxml.jackson.dataformat.csv.CsvGenerator
import java.io.{Reader, Writer}
import kantan.codecs.resource.ResourceIterator
import kantan.csv.{CsvConfiguration, CsvWriter}

/** Provides CSV reader and writer engines using [[https://github.com/FasterXML/jackson-dataformat-csv jackson.csv]].
  *
  * Importing `kantan.csv.engine.jackson._` will replace default engines by the jackson-backed ones. If you need to
  * tweak how jackson.csv behaves, however, you can handcraft engines though [[readerEngineFrom]] and
  * [[writerEngineFrom]].
  */
object jackson {
  // - Schema ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  type CsvSchema = com.fasterxml.jackson.dataformat.csv.CsvSchema
  type CsvMapper = com.fasterxml.jackson.dataformat.csv.CsvMapper

  type MappingIteratorBuilder = (Reader, CsvConfiguration) ⇒ MappingIterator[Array[String]]
  type SequenceWriterBuilder = (Writer, CsvConfiguration) ⇒ SequenceWriter

  private val MAPPER: CsvMapper = new CsvMapper()
  MAPPER.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.WRAP_AS_ARRAY)
  MAPPER.enable(com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING)


  // - Reader engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultMappingIteratorBuilder: MappingIteratorBuilder = (reader, conf) ⇒ {
    MAPPER.readerFor(classOf[Array[String]]).`with`(MAPPER.schemaFor(classOf[Array[String]])
      .withColumnSeparator(conf.columnSeparator).withQuoteChar(conf.quote)).readValues(reader)
  }

  /** Creates a new `ReaderEngine` from the specified [[MappingIteratorBuilder]].
    *
    * The purpose of this is to let developers use some of the jackson.csv features that kantan.csv does not expose
    * through its public API.
    */
  def readerEngineFrom(f: MappingIteratorBuilder): ReaderEngine =
    ReaderEngine.from { (r, s) ⇒ ResourceIterator.fromIterator(f(r, s)) }

  /** Default jackson.csv `ReaderEngine`.
    *
    * It's possible to tweak the behaviour of the underlying writer through [[readerEngineFrom]].
    */
  implicit val jacksonCsvReaderEngine: ReaderEngine = readerEngineFrom(defaultMappingIteratorBuilder)



  // - Writer engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultSequenceWriterBuilder: SequenceWriterBuilder = (writer, conf) ⇒ {
    val mapper =
      if(conf.quotePolicy == CsvConfiguration.QuotePolicy.WhenNeeded) MAPPER
      else MAPPER.copy().enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)

    mapper.writer.`with`(mapper.schemaFor(classOf[Array[String]]).withColumnSeparator(conf.columnSeparator)
            .withQuoteChar(conf.quote).withLineSeparator("\r\n").withoutComments).writeValues(writer)
  }

  /** Creates a new `WriterEngine` from the specified [[SequenceWriterBuilder]].
    *
    * The purpose of this is to let developers use some of the jackson.csv features that kantan.csv does not expose
    * through its public API.
    */
  def writerEngineFrom(f: SequenceWriterBuilder): WriterEngine = WriterEngine.from { (w, s) ⇒
    CsvWriter(f(w, s)) { (out, ss) ⇒
      out.write(ss.toArray)
      ()
    }(_.close())
  }

  /** Default jackson.csv `WriterEngine`.
    *
    * It's possible to tweak the behaviour of the underlying writer through [[writerEngineFrom]].
    */
  implicit val jacksonCsvWriterEngine: WriterEngine = writerEngineFrom(defaultSequenceWriterBuilder)
}
