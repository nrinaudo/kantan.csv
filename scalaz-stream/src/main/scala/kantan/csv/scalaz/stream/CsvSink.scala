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

package kantan.csv.scalaz.stream

import java.io.Writer
import kantan.csv.{CsvWriter, RowEncoder}
import kantan.csv.{CsvSink => CSink}
import kantan.csv.engine.WriterEngine
import scalaz.concurrent.Task
import scalaz.stream._

/** Turns instances of `S` into CSV sinks.
  *
  * Any type `S` that has a implicit instance of `kantan.csv.CsvSink` in scope will be enriched by the `asCsvSink`
  * method (which maps to [[sink]]).
  *
  * Additionally, any type that has an instance of `kantan.csv.CsvSink` in scope automatically gets an instance of
  * [[CsvSink]].
  */
trait CsvSink[S] extends Serializable {
  def writer(s: S): Writer

  def sink[A: RowEncoder](s: S, sep: Char, header: Seq[String] = Seq.empty)(implicit e: WriterEngine): Sink[Task, A] =
    CsvSink[A](writer(s), sep, header:_*)
}

object CsvSink {
  def apply[A](implicit ev: CsvSink[A]): CsvSink[A] = macro imp.summon[CsvSink[A]]

  def apply[A](writer: ⇒ CsvWriter[A]): Sink[Task, A] =
    io.resource(Task.delay(writer))(out ⇒ Task.delay(out.close())) { out ⇒
      Task.now((a: A) ⇒ Task.delay { out.write(a); () })
    }

  def apply[A: RowEncoder](writer: ⇒ Writer, sep: Char, header: String*)
                          (implicit e: WriterEngine): Sink[Task, A] =
    CsvSink(CsvWriter[A](writer, sep, header:_*))

  implicit def fromOutput[S: CSink]: CsvSink[S] = new CsvSink[S] {
    override def writer(s: S) = CSink[S].open(s)
  }
}
