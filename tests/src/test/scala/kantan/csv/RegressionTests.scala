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

package kantan.csv

import org.scalatest.FunSuite

class RegressionTests extends FunSuite {
  // - CellCodec instances for non default types (#36) -----------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  class A
  class B

  implicit val cellCodecA: CellCodec[A] = CellCodec.from(_ ⇒ DecodeResult.success(new A))(_ ⇒ "")
  implicit val cellCodecB: CellCodec[B] = CellCodec.from(_ ⇒ DecodeResult.success(new B))(_ ⇒ "")

  test("Option decoders should be available for types that do not have a StringDecoder") {
    CellDecoder[Option[A]]
    ()
  }

  test("Option encoder should be available for types that do not have a StringEncoder") {
    CellEncoder[Option[A]]
    ()
  }

  test("Either decoders should be available for types that do not have a StringDecoder") {
    CellDecoder[Either[A, B]]
    ()
  }

  test("Either encoders should be available for types that do not have a StringEncoder") {
    CellEncoder[Either[A, B]]
    ()
  }

  test("Trailing optional cells should decode as expected (https://github.com/nrinaudo/kantan.csv/issues/53)") {
    import kantan.csv.ops._

    assert("1,a,100\n2,b".unsafeReadCsv[List, (Int, String, Option[Int])](',', false) ==
      List((1, "a", Some(100)), (2, "b", None))
    )
  }
}
