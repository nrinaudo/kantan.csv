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

package kantan.csv.benchmark

import kantan.csv.ops._
import kantan.csv.rfc
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EncodingTests extends AnyFunSuite with Matchers {
  val encoding = new Encoding

  def decode(str: String): List[CsvEntry] = str.unsafeReadCsv[List, CsvEntry](rfc)

  test("kantan internal") {
    decode(encoding.kantanInternal) should be(rawData)
  }

  test("kantan jackson") {
    decode(encoding.kantanJackson) should be(rawData)
  }

  test("kantan commons") {
    decode(encoding.kantanCommons) should be(rawData)
  }

  test("jackson") {
    decode(encoding.jackson) should be(rawData)
  }

  test("commons") {
    decode(encoding.commons) should be(rawData)
  }

  test("opencsv") {
    decode(encoding.opencsv) should be(rawData)
  }

  test("univocity") {
    decode(encoding.univocity) should be(rawData)
  }

  test("scala-csv") {
    decode(encoding.scalaCsv) should be(rawData)
  }
}
