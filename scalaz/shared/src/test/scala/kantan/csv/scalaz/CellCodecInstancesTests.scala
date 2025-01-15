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

package kantan.csv.scalaz

import kantan.codecs.scalaz.laws.discipline.ScalazDisciplineSuite
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.DecodeError
import kantan.csv.scalaz.arbitrary._
import kantan.csv.scalaz.equality._
import scalaz.scalacheck.ScalazProperties.contravariant
import scalaz.scalacheck.ScalazProperties.monadError
import scalaz.scalacheck.ScalazProperties.plus
import scalaz.std.anyVal._
import scalaz.std.string._

class CellCodecInstancesTests extends ScalazDisciplineSuite {

  checkAll("CellDecoder", monadError.laws[CellDecoder, DecodeError])
  checkAll("CellDecoder", plus.laws[CellDecoder])
  checkAll("CellEncoder", contravariant.laws[CellEncoder])

}
