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

import org.scalatest.{FunSuite, Matchers}

class RegressionTests extends FunSuite with Matchers {
  // - CellCodec instances for non default types (#36) -----------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  class A
  class B

  implicit val cellCodecA: CellCodec[A] = CellCodec.from(_ => DecodeResult.success(new A))(_ => "")
  implicit val cellCodecB: CellCodec[B] = CellCodec.from(_ => DecodeResult.success(new B))(_ => "")

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

  test("Trailing optional cells should decode as expected (#53)") {
    import kantan.csv.ops._

    "1,a,100\n2,b".unsafeReadCsv[List, (Int, String, Option[Int])](rfc) should be(
      List((1, "a", Some(100)), (2, "b", None))
    )
  }

  test("Decoding to Seq shouldn't leak out mutability") {
    import kantan.csv.ops._

    // See #181, this used to yield Seq(Seq(3), Seq(3), Seq(3))
    "1\n2\n3\n".unsafeReadCsv[Seq, Seq[String]](rfc) should be(Seq(Seq("1"), Seq("2"), Seq("3")))
  }

  test("the correct missing header should show up in an error during header decoding") {
    import kantan.csv.ops._
    import kantan.csv.DecodeError.TypeError

    case class Foo(header1: String, header2: Int)

    implicit val decoder: HeaderDecoder[Foo] = HeaderDecoder.decoder("head_1", "head_2")(Foo.apply)

    val csv1    = "head_2\n3"
    val result1 = csv1.readCsv[List, Foo](rfc.withHeader)
    result1 should be(List(Left(TypeError("Missing header(s): head_1"))))

    val csv2    = "head_1\nvalue1"
    val result2 = csv2.readCsv[List, Foo](rfc.withHeader)
    result2 should be(List(Left(TypeError("Missing header(s): head_2"))))
  }
}
