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

package kantan.csv

import java.io.Reader
import kantan.csv.engine.ReaderEngine

/** Provides instance creation and summoning methods. */
object CsvReader {
  /** Creates a new instance of [[CsvReader]].
    *
    * @param in where to read data from
    * @param open function that turns `in` into an iterator.
    * @param cls function that closes `in` when no longer needed.
    */
  // TODO: open is unsafe, should return a Result[F, Iterator[R]]
  def fromResource[I, R](in: I)(open: I ⇒ Iterator[R])(cls: I ⇒ Unit): CsvReader[ParseResult[R]] =
    new CsvReader[ParseResult[R]] {
      val it = open(in)
      override def checkNext = it.hasNext
      override def readNext() =
        if(it.hasNext) ParseResult(it.next())
        else           ParseResult.noSuchElement
      override def release() = cls(in)
    }

  /** Opens a [[CsvReader]] on the specified `Reader`.
    *
    * @param reader what to parse as CSV
    * @param sep column separator
    * @param header whether or not to skip the first row
    */
  def apply[A](reader: Reader, sep: Char, header: Boolean)
              (implicit da: RowDecoder[A], e: ReaderEngine): CsvReader[ReadResult[A]] = {
    val data: CsvReader[ReadResult[Seq[String]]] = e.readerFor(reader, sep)

    if(header && data.hasNext) data.next()

    data.map(_.flatMap(da.decode))
  }
}
