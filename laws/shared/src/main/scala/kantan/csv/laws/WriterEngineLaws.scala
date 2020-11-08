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

package kantan.csv.laws

import kantan.csv.engine.WriterEngine
import kantan.csv.ops._
import kantan.csv.rfc

trait WriterEngineLaws extends RfcWriterLaws {
  def quoteAll(csv: List[List[Int]]): Boolean = {
    val data = csv.filter(_.nonEmpty)

    data.asCsv(rfc.quoteAll).trim == data.map(_.map(i => s""""${i.toString}"""").mkString(",")).mkString("\r\n")
  }

  def columnSeparator(csv: List[List[Cell]], c: Char): Boolean =
    roundTripFor(csv, rfc.withCellSeparator(c))
}

object WriterEngineLaws {
  def apply(e: WriterEngine): WriterEngineLaws = new WriterEngineLaws {
    override implicit val engine: WriterEngine = e
  }
}
