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

package kantan.csv.java8

import java.time.ZonedDateTime
import kantan.csv._
import kantan.csv.java8.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ZonedDateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // This is apparently necessary for Scala 2.10
  implicit val decoder: CellDecoder[ZonedDateTime] = defaultZonedDateTimeDecoder.value
  implicit val encoder: CellEncoder[ZonedDateTime] = defaultZonedDateTimeEncoder.value

  checkAll("CellCodec[ZonedDateTime]", CellCodecTests[ZonedDateTime].codec[String, Float])
  checkAll("RowCodec[ZonedDateTime]", RowCodecTests[ZonedDateTime].codec[String, Float])
}
