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

import kantan.csv.ops._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream

class HeaderDecoderZipTests extends AnyFunSuite with Matchers {
  val x: HeaderDecoder[String] = HeaderDecoder.decoder[String, String]("A")(identity)
  val y: HeaderDecoder[String] = HeaderDecoder.decoder[String, String]("B")(identity)
  val z: HeaderDecoder[String] = HeaderDecoder.decoder[String, String]("C")(identity)

  test("Zipping two header decoders should result in a union of the two") {
    implicit val decoder: HeaderDecoder[(String, String)] = x ~ y

    val csv =
      """A,B
        |foo,bar
        |far,baz""".stripMargin

    val lines = new ByteArrayInputStream(csv.getBytes)
      .asCsvReader[(String, String)](rfc.withHeader)
      .toList

    lines should be(List(Right("foo" -> "bar"), Right("far" -> "baz")))
  }

  test("Zipping produces a flattened tuple") {
    implicit val decoder: HeaderDecoder[(String, String, String)] = x ~ y ~ z

    val csv =
      """B,A,C
        |foo,bar,baz
        |far,baz,bau""".stripMargin

    val lines = new ByteArrayInputStream(csv.getBytes)
      .asCsvReader[(String, String, String)](rfc.withHeader)
      .toList

    lines should be(List(Right(("bar", "foo", "baz")), Right(("baz", "far", "bau"))))
  }

  test("Can ignore missing columns") {
    implicit val decoder: HeaderDecoder[(String, Option[String], String)] = x ~ y.optional ~ z

    val csv =
      """A,C
        |foo,bar
        |far,baz""".stripMargin

    val lines = new ByteArrayInputStream(csv.getBytes)
      .asCsvReader[(String, Option[String], String)](rfc.withHeader)
      .toList

    lines should be(List(Right(("foo", None, "bar")), Right(("far", None, "baz"))))
  }

  test("optional operator is idempotent") {
    implicit val decoder: HeaderDecoder[(String, Option[String], String)] = x ~ y.optional.optional ~ z

    val csv =
      """A,C
        |foo,bar
        |far,baz""".stripMargin

    val lines = new ByteArrayInputStream(csv.getBytes)
      .asCsvReader[(String, Option[String], String)](rfc.withHeader)
      .toList

    lines should be(List(Right(("foo", None, "bar")), Right(("far", None, "baz"))))
  }
}
