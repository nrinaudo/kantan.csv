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

import org.scalatest.FunSuite

class DecodingTests extends FunSuite {
  val decoding = new Decoding

  test("kantan internal") {
    assert(decoding.kantanInternal == rawData)
  }

  test("kantan jackson") {
    assert(decoding.kantanJackson == rawData)
  }

  test("kantan commons") {
    assert(decoding.kantanCommons == rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("kantan opencsv") {
    assert(decoding.kantanOpenCsv == rawData)
  }
  */

  test("jackson") {
    assert(decoding.jackson == rawData)
  }

  test("commons") {
    assert(decoding.commons == rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("opencsv") {
    assert(decoding.opencsv == rawData)
  }
  */

  test("product-collections") {
    assert(decoding.productCollections == rawData)
  }

  test("univocity") {
    assert(decoding.univocity == rawData)
  }

  test("scala-csv") {
    assert(decoding.scalaCsv == rawData)
  }
}
