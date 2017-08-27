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

package kantan.csv.engine

import kantan.csv.ops._
import kantan.csv.rfc
import org.scalatest.{FunSuite, Matchers}

class InternalReaderRegressionTests extends FunSuite with Matchers {
  test("cell with whitespace") {
    "abc, ".unsafeReadCsv[List, List[String]](rfc) should be(List(List("abc", " ")))
    "abc ,def".unsafeReadCsv[List, List[String]](rfc) should be(List(List("abc ", "def")))

    "abc, \n".unsafeReadCsv[List, List[String]](rfc) should be(List(List("abc", " ")))
    "abc ,def\n".unsafeReadCsv[List, List[String]](rfc) should be(List(List("abc ", "def")))
  }

  test("CRLF in escaped") {
    "1\r\n\"Once upon\r\na time\"".unsafeReadCsv[List, List[String]](rfc) should be(
      List(List("1"), List("Once upon\r\na time"))
    )
  }

  test("If the last cell of the last row is a quoted empty string, it should still be read (#88)") {
    "\"\"".unsafeReadCsv[List, List[String]](rfc) should be(List(List("")))
  }

  test("if the last cell of a row is quoted and followed by a space, it should still be read (#89)") {
    """ "field1","field2","field3" """.unsafeReadCsv[List, List[String]](rfc) should be(
      List(List("field1", "field2", "field3 "))
    )
  }
}
