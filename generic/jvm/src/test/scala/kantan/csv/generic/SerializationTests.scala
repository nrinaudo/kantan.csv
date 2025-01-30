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

package kantan.csv.generic

import kantan.codecs.shapeless.laws.Or
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import kantan.csv.generic.Instances._
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.SerializableTests

// Shapeless' Lazy generates code with Null that we need to ignore.
@SuppressWarnings(Array("org.wartremover.warts.Null"))
class SerializationTests extends DisciplineSuite {

  checkAll("CellDecoder[Or[Int, Boolean]]", SerializableTests[CellDecoder[Int Or Boolean]].serializable)
  checkAll("CellEncoder[Or[Int, Boolean]]", SerializableTests[CellEncoder[Int Or Boolean]].serializable)

  checkAll("RowDecoder[Or[Complex, Simple]]", SerializableTests[RowDecoder[Or[Complex, Simple]]].serializable)
  checkAll("RowEncoder[Or[Complex, Simple]]", SerializableTests[RowEncoder[Or[Complex, Simple]]].serializable)

}
