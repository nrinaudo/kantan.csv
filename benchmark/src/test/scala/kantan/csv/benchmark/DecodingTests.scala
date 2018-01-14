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
package benchmark

import org.scalatest.{FunSuite, Matchers}

class DecodingTests extends FunSuite with Matchers {
  val decoding = new Decoding

  test("kantan internal") {
    decoding.kantanInternal should be(rawData)
  }

  test("kantan jackson") {
    decoding.kantanJackson should be(rawData)
  }

  test("kantan commons") {
    decoding.kantanCommons should be(rawData)
  }

  test("jackson") {
    decoding.jackson should be(rawData)
  }

  test("commons") {
    decoding.commons should be(rawData)
  }

  // TODO: broken: opencsv doesn't play nice with CRLF in quoted cells.
  /*
  test("opencsv") {
    assert(decoding.opencsv == rawData)
  }
   */

  test("product-collections") {
    decoding.productCollections should be(rawData)
  }

  test("univocity") {
    decoding.univocity should be(rawData)
  }

  test("scala-csv") {
    decoding.scalaCsv should be(rawData)
  }
}
