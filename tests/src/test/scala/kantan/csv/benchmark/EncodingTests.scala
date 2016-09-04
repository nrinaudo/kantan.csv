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

package kantan.csv.benchmark

import kantan.csv.ops._
import org.scalatest.FunSuite

class EncodingTests extends FunSuite {
  val encoding = new Encoding

  def decode(str: String): List[CsvEntry] = str.unsafeReadCsv[List, CsvEntry](',', false)

  test("kantan internal") {
    assert(decode(encoding.kantanInternal) == rawData)
  }

  test("kantan jackson") {
    assert(decode(encoding.kantanJackson) == rawData)
  }

  test("kantan commons") {
    assert(decode(encoding.kantanCommons) == rawData)
  }

  test("kantan opencsv") {
    assert(decode(encoding.kantanOpenCsv) == rawData)
  }

  test("jackson") {
    assert(decode(encoding.jackson) == rawData)
  }

  test("commons") {
    assert(decode(encoding.commons) == rawData)
  }

  test("opencsv") {
    assert(decode(encoding.opencsv) == rawData)
  }

  test("product-collections") {
    assert(decode(encoding.productCollections) == rawData)
  }

  test("univocity") {
    assert(decode(encoding.univocity) == rawData)
  }

  test("scala-csv") {
    assert(decode(encoding.scalaCsv) == rawData)
  }
}
