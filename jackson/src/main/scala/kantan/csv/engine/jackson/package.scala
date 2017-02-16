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

import com.fasterxml.jackson.dataformat.csv.CsvSchema
import kantan.codecs.resource.ResourceIterator
import kantan.csv._

package object jackson {
  // - Schema ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Type of functions that create a `CSVSchema` instance from a given column separator. */
  type CSVSchemaBuilder = Char ⇒ CsvSchema


  // - Reader engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[ReaderEngine]] from the specified [[CSVSchemaBuilder]].
    *
    * The purpose of this is to let developers use some of the jackson.csv features that kantan.csv does not expose
    * through its public API.
    */
  def readerEngineFrom(f: CSVSchemaBuilder): ReaderEngine =
    ReaderEngine { (r, s) ⇒ ResourceIterator.fromIterator(JacksonCsv.parse(r, f(s))) }

  /** Default jackson.csv [[ReaderEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[readerEngineFrom]].
    */
  implicit val jacksonCsvReaderEngine: ReaderEngine = readerEngineFrom(s ⇒ JacksonCsv.defaultParserSchema(s))



  // - Writer engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[WriterEngine]] from the specified [[CSVSchemaBuilder]].
    *
    * The purpose of this is to let developers use some of the jackson.csv features that kantan.csv does not expose
    * through its public API.
    */
  def writerEngineFrom(f: CSVSchemaBuilder): WriterEngine = WriterEngine { (w, s) ⇒
    CsvWriter(JacksonCsv.write(w, f(s))) { (out, ss) ⇒
      out.write(ss.toArray)
      ()
    }(_.close())
  }

  /** Default jackson.csv [[WriterEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[writerEngineFrom]].
    */
  implicit val jacksonCsvWriterEngine: WriterEngine = writerEngineFrom(s ⇒ JacksonCsv.defaultWriterSchema(s))
}
