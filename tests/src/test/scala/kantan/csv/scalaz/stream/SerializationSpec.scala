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

import java.io.StringWriter
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.scalaz.stream.ops._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scalaz.concurrent.Task
import scalaz.stream.{Cause, Process}
import scalaz.stream.Process._

class SerializationSpec extends FunSuite with GeneratorDrivenPropertyChecks {
  def read(raw: String): List[List[String]] =
    raw.asUnsafeCsvSource[List[String]](',', false).runLog.unsafePerformSync.toList

  def write(data: List[List[String]]): String = {
    val sw = new StringWriter()
    val iterator = data.iterator

    Process.repeatEval(Task.delay { if(iterator.hasNext) iterator.next() else throw Cause.Terminated(Cause.End) })
      .to(CsvSink[List[String]](sw, ',')).run.unsafePerformSync

    sw.toString
  }

  test("Serialized CSV data should be parsed correctly") {
    forAll(csv) { ss: List[List[String]] ⇒
      assert(read(write(ss)) == ss)
    }
  }
}
