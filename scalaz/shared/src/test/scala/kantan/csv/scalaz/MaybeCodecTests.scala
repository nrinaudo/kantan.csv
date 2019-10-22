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

import _root_.scalaz.Maybe
import kantan.csv.scalaz.arbitrary._
import laws.discipline._

class MaybeCodecTests extends DisciplineSuite {

  import org.scalacheck.Arbitrary
  import kantan.csv.laws._

  // These 2 implicits are not found in 2.13. I'm not sure why - it *might* have to do with the change in import
  // statements behaviour?
  implicit val ai: Arbitrary[IllegalRow[Maybe[(Int, Int)]]] = arbIllegalValueFromDec
  implicit val al: Arbitrary[LegalRow[Maybe[(Int, Int)]]]   = arbLegalValueFromEnc

  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].codec[String, Float])
  checkAll("Maybe[(Int, Int)]", RowCodecTests[Maybe[(Int, Int)]].codec[String, Float])
}
