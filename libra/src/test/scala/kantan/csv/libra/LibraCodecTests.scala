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

package kantan.csv.libra

import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.discipline.SerializableTests
import kantan.csv.libra.arbitrary._
import libra.Quantity
import shapeless.HNil

class LibraCodecTests extends DisciplineSuite {

  checkAll("CellDecoder[Quantity[Double, HNil]]", SerializableTests[CellDecoder[Quantity[Double, HNil]]].serializable)
  checkAll("RowDecoder[Quantity[Int, HNil]]", SerializableTests[RowDecoder[Quantity[Int, HNil]]].serializable)

  checkAll("CellEncoder[Quantity[Double, HNil]]", SerializableTests[CellEncoder[Quantity[Double, HNil]]].serializable)
  checkAll("RowEncoder[Quantity[Int, HNil]]", SerializableTests[RowEncoder[Quantity[Int, HNil]]].serializable)

  checkAll("CellCodec[Quantity[Double, HNil]]", CellCodecTests[Quantity[Double, HNil]].codec[String, Float])
  checkAll("CellCodec[Quantity[Int, HNil]]", CellCodecTests[Quantity[Int, HNil]].codec[String, Float])

  checkAll("RowCodec[Quantity[Double, HNil]]", RowCodecTests[Quantity[Double, HNil]].codec[String, Float])
  checkAll("RowCodec[Quantity[Int, HNil]]", RowCodecTests[Quantity[Int, HNil]].codec[String, Float])

}
