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

package kantan.csv.scalaz.stream.ops

import _root_.scalaz.concurrent.Task
import _root_.scalaz.stream.Sink
import kantan.csv._
import kantan.csv.engine.WriterEngine
import kantan.csv.scalaz.stream.CsvSink

class CsvSinkOps[A](val a: A) extends AnyVal {
  def asCsvSink[B: RowEncoder](sep: Char, header: Seq[String] = Seq.empty)
                              (implicit oa: CsvSink[A], e: WriterEngine): Sink[Task, B] =
    oa.sink(a, sep, header)

}

trait ToCsvSinkOps {
  implicit def toCsvSinkOps[A](a: A): CsvSinkOps[A] = new CsvSinkOps[A](a)
}

object csvSink extends ToCsvSinkOps
