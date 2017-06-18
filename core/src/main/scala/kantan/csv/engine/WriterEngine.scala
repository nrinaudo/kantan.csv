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

package kantan.csv.engine

import java.io.Writer
import kantan.csv.{CsvConfiguration, CsvWriter}

/** Provides factory-like services for [[CsvWriter]].
  *
  * Functions that create instances of [[CsvWriter]], either as part of their return types or for internal operations,
  * can declare an implicit [[CsvWriter]] parameter. If one is imported explicitly (such as the Jackson engine), it will
  * be used. Otherwise, the [[WriterEngine$.internalCsvWriterEngine internal]] one is always in scope.
  *
  * @see [[ReaderEngine]]
  */
trait WriterEngine {
  /** Creates a new instance of [[CsvWriter]] that writes encoded data to the specified writer.
    *
    * @param writer where to write encoded data.
    * @param conf column separator.
    */
  def writerFor(writer: Writer, conf: CsvConfiguration): CsvWriter[Seq[String]]
}

/** Provides creation methods and default implementations. */
object WriterEngine {
  /** Creates a new instance of [[WriterEngine]] that wraps the specified function. */
  def from(f: (Writer, CsvConfiguration) ⇒ CsvWriter[Seq[String]]): WriterEngine = new WriterEngine {
    override def writerFor(writer: Writer, conf: CsvConfiguration): CsvWriter[Seq[String]] = f(writer, conf)
  }

  /** Default engine, returns an instance of the internal [[CsvWriter]].
    *
    * This instance is always in scope and will be used if no other is explicitly imported.
    */
  implicit val internalCsvWriterEngine: WriterEngine = WriterEngine.from((w, c) ⇒ new InternalWriter(w, c))
}
