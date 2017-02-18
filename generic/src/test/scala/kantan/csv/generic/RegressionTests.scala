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

import org.scalatest.FunSuite

class RegressionTests extends FunSuite {
  test("Implicit resolution should not be impacted by the presence of default arguments (#65)") {
    case class Foobar(i: Int)

    object Test {
      import kantan.csv.ops._

      List(Foobar(3)).asCsv()
    }
  }
}

