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

import java.io.File
import laws.discipline._, arbitrary._

class FileCodecTests extends DisciplineSuite {

  checkAll("CellEncoder[File]", SerializableTests[CellEncoder[File]].serializable)
  checkAll("CellDecoder[File]", SerializableTests[CellDecoder[File]].serializable)

  checkAll("RowEncoder[File]", SerializableTests[RowEncoder[File]].serializable)
  checkAll("RowDecoder[File]", SerializableTests[RowDecoder[File]].serializable)

  checkAll("CellCodec[File]", CellCodecTests[File].bijectiveCodec[Int, Float])

}
