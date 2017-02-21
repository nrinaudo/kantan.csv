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

package kantan.csv.generic

import kantan.codecs.shapeless.laws._
import kantan.csv.generic.arbitrary._
import kantan.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

// Shapeless' Lazy generates code with Null that we need to ignore.
@SuppressWarnings(Array("org.wartremover.warts.Null"))
class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Or[Int, Boolean]]", CellCodecTests[Int Or Boolean].codec[Byte, String])
}
