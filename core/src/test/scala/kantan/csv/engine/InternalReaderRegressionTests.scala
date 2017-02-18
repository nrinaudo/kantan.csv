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

package kantan.csv.engine

import kantan.csv.ops._
import org.scalatest.FunSuite

class InternalReaderRegressionTests extends FunSuite {
  test("cell with whitespace") {
    assert("abc, ".unsafeReadCsv[List, List[String]]() == List(List("abc", " ")))
    assert("abc ,def".unsafeReadCsv[List, List[String]]() == List(List("abc ", "def")))

    assert("abc, \n".unsafeReadCsv[List, List[String]]() == List(List("abc", " ")))
    assert("abc ,def\n".unsafeReadCsv[List, List[String]]() == List(List("abc ", "def")))
  }

  test("CRLF in escaped") {
    assert("1\r\n\"Once upon\r\na time\"".unsafeReadCsv[List, List[String]]() ==
           List(List("1"), List("Once upon\r\na time")))
  }
}
