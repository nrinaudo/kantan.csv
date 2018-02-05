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
package scalaz

import _root_.scalaz._, Scalaz._
import _root_.scalaz.scalacheck.ScalazProperties._
import arbitrary._, equality._
import kantan.codecs.scalaz.laws.discipline.ScalazDisciplineSuite

class CodecTests extends ScalazDisciplineSuite {

  // scalaz doesn't provide an Eq[Seq] instance, mostly because Seq isn't a very meaningfull type.
  implicit def seqEq[A: Equal]: Equal[Seq[A]] = Equal[List[A]].contramap(_.toList)

  checkAll("CellDecoder", monadError.laws[CellDecoder, DecodeError])
  checkAll("RowDecoder", monadError.laws[RowDecoder, DecodeError])

  checkAll("CellDecoder", plus.laws[CellDecoder])
  checkAll("RowDecoder", plus.laws[RowDecoder])

  checkAll("CellEncoder", contravariant.laws[CellEncoder])
  checkAll("RowEncoder", contravariant.laws[RowEncoder])

}
