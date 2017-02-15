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
  def readerFrom(f: Char ⇒ CsvSchema): ReaderEngine =
    ReaderEngine { (r, s) ⇒ ResourceIterator.fromIterator(JacksonCsv.parse(r, f(s))) }

  implicit val reader: ReaderEngine = readerFrom(s ⇒ JacksonCsv.defaultParserSchema(s))

  def writerFrom(f: Char ⇒ CsvSchema): WriterEngine = WriterEngine { (w, s) ⇒
    CsvWriter(JacksonCsv.write(w, f(s))) { (out, ss) ⇒
      out.write(ss.toArray)
      ()
    }(_.close())
  }

  implicit val writer: WriterEngine = writerFrom(s ⇒ JacksonCsv.defaultWriterSchema(s))
}
