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

import com.opencsv.{CSVReader, CSVWriter}
import java.io.{Reader, Writer}
import kantan.codecs.resource.ResourceIterator
import kantan.csv._

// TODO: known bugs
// - \r\n is not preserved within quoted cells, it's turned into \n (csv spectrum test)
// - unescaped double quotes are not supported.

package object opencsv {
  // - Type aliases ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Type of functions that turn a `Reader` and a separator character and turn them into a `CSVReader` .*/
  type CSVReaderBuilder = (Reader, Char) ⇒ CSVReader

  /** Type of functions that turn a `Writer` and a separator character and turn them into a `CSVWriter` .*/
  type CSVWriterBuilder = (Writer, Char) ⇒ CSVWriter



  // - ReaderEngine ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Note that using the `null` character as escape is a bit of a cheat, but it kind of works, mostly. Escaping is not
  // part of the CSV format, but I found no other way to disable it.
  def defaultReaderEngine(reader: Reader, sep: Char): CSVReader =
    new CSVReader(reader, sep, '"', '\u0000', 0, false, false, false)


  /** Creates a new [[ReaderEngine]] from the specified [[CSVReaderBuilder]].
    *
    * The purpose of this is to let developers use some of the open-csv features that kantan.csv does not expose through
    * its public API.
    */
  def readerEngineFrom(f: CSVReaderBuilder): ReaderEngine = ReaderEngine { (r, s) ⇒
    ResourceIterator.fromIterator(f(r, s).iterator())
  }

  /** Default open-csv [[ReaderEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[readerEngineFrom]].
    */
  implicit val openCsvReaderEngine: ReaderEngine = readerEngineFrom(defaultReaderEngine)


  // - WriterEngine ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def defaultWriterEngine(writer: Writer, sep: Char): CSVWriter = new CSVWriter(writer, sep, '"', "\r\n")

  /** Creates a new [[WriterEngine]] from the specified [[CSVWriterBuilder]].
    *
    * The purpose of this is to let developers use some of the open-csv features that kantan.csv does not expose through
    * its public API.
    */
  def writerEngineFrom(f: CSVWriterBuilder): WriterEngine = WriterEngine { (w, s) ⇒
    CsvWriter(f(w, s))((out, ss) ⇒ out.writeNext(ss.toArray))(_.close())
  }

  /** Default open-csv [[WriterEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[writerEngineFrom]].
    */
  implicit val openCsvWriterEngine: WriterEngine = writerEngineFrom(defaultWriterEngine)
}
