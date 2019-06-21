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

package kantan.csv
package engine

import java.io.Reader
import kantan.codecs.resource.ResourceIterator

/** Provides kantan.csv with CSV parsing functionality.
  *
  * All methods that will need to create a new instance of [[CsvReader]] expect and rely on an implicit [[ReaderEngine]]
  * parameter. This allows third party libraries to plug into kantan.csv and replace the default parser at the cost
  * of a simple import.
  */
trait ReaderEngine {

  /** Turns the specified `Reader` into a [[CsvReader]]. */
  def unsafeReaderFor(reader: Reader, conf: CsvConfiguration): CsvReader[Seq[String]]

  /** Turns the specified `Reader` into a safe [[CsvReader]]. */
  def readerFor(reader: => Reader, conf: CsvConfiguration): CsvReader[ReadResult[Seq[String]]] =
    ParseResult(unsafeReaderFor(reader, conf))
      .map(_.safe(ParseError.NoSuchElement: ParseError)(e => ParseError.IOError(e)))
      .left
      .map(e => ResourceIterator(ReadResult.failure(e)))
      .merge
      .withClose(() => reader.close())
}

/** Provides instance creation methods and default implementations. */
object ReaderEngine {

  /** Creates a new [[ReaderEngine]] instance. */
  def from(f: (Reader, CsvConfiguration) => CsvReader[Seq[String]]): ReaderEngine = new ReaderEngine {
    override def unsafeReaderFor(reader: Reader, conf: CsvConfiguration): CsvReader[Seq[String]] = f(reader, conf)
  }

  /** Default reader engine, used whenever a custom one is not explicitly brought in scope. */
  implicit val internalCsvReaderEngine: ReaderEngine = ReaderEngine.from(InternalReader.apply)
}
