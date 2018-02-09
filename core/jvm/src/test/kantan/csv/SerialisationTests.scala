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

import laws.discipline._, arbitrary._

class SerialisationTests extends DisciplineSuite {

  checkAll("CellEncoder[BigDecimal]", SerializableTests[CellEncoder[BigDecimal]].serializable)
  checkAll("CellDecoder[BigDecimal]", SerializableTests[CellDecoder[BigDecimal]].serializable)
  checkAll("RowEncoder[BigDecimal]", SerializableTests[RowEncoder[BigDecimal]].serializable)
  checkAll("RowDecoder[BigDecimal]", SerializableTests[RowDecoder[BigDecimal]].serializable)


  checkAll("CellEncoder[BigInt]", SerializableTests[CellEncoder[BigInt]].serializable)
  checkAll("CellDecoder[BigInt]", SerializableTests[CellDecoder[BigInt]].serializable)
  checkAll("RowEncoder[BigInt]", SerializableTests[RowEncoder[BigInt]].serializable)
  checkAll("RowDecoder[BigInt]", SerializableTests[RowDecoder[BigInt]].serializable)


  checkAll("CellEncoder[Boolean]", SerializableTests[CellEncoder[Boolean]].serializable)
  checkAll("CellDecoder[Boolean]", SerializableTests[CellDecoder[Boolean]].serializable)
  checkAll("RowEncoder[Boolean]", SerializableTests[RowEncoder[Boolean]].serializable)
  checkAll("RowDecoder[Boolean]", SerializableTests[RowDecoder[Boolean]].serializable)


  checkAll("CellEncoder[Byte]", SerializableTests[CellEncoder[Byte]].serializable)
  checkAll("CellDecoder[Byte]", SerializableTests[CellDecoder[Byte]].serializable)
  checkAll("RowEncoder[Byte]", SerializableTests[RowEncoder[Byte]].serializable)
  checkAll("RowDecoder[Byte]", SerializableTests[RowDecoder[Byte]].serializable)


  checkAll("CellEncoder[Char]", SerializableTests[CellEncoder[Char]].serializable)
  checkAll("CellDecoder[Char]", SerializableTests[CellDecoder[Char]].serializable)
  checkAll("RowEncoder[Char]", SerializableTests[RowEncoder[Char]].serializable)
  checkAll("RowDecoder[Char]", SerializableTests[RowDecoder[Char]].serializable)


  implicit val codec: CellCodec[Date] =
    CellCodec.dateCodec(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH))

  checkAll("CellEncoder[Date]", SerializableTests[CellEncoder[Date]].serializable)
  checkAll("CellDecoder[Date]", SerializableTests[CellDecoder[Date]].serializable)
  checkAll("RowEncoder[Date]", SerializableTests[RowEncoder[Date]].serializable)
  checkAll("RowDecoder[Date]", SerializableTests[RowDecoder[Date]].serializable)


  checkAll("CellEncoder[Double]", SerializableTests[CellEncoder[Double]].serializable)
  checkAll("CellDecoder[Double]", SerializableTests[CellDecoder[Double]].serializable)
  checkAll("RowEncoder[Double]", SerializableTests[RowEncoder[Double]].serializable)
  checkAll("RowDecoder[Double]", SerializableTests[RowDecoder[Double]].serializable)


  checkAll("CellEncoder[Either[Int, Boolean]]", SerializableTests[CellEncoder[Either[Int, Boolean]]].serializable)
  checkAll("CellDecoder[Either[Int, Boolean]]", SerializableTests[CellDecoder[Either[Int, Boolean]]].serializable)
  checkAll(
    "RowEncoder[Either[(Int, Int, Int), Boolean]]",
    SerializableTests[RowEncoder[Either[(Int, Int, Int), Boolean]]].serializable
  )
  checkAll(
    "RowDecoder[Either[(Int, Int, Int), Boolean]]",
    SerializableTests[RowDecoder[Either[(Int, Int, Int), Boolean]]].serializable
  )


  checkAll("CellEncoder[File]", SerializableTests[CellEncoder[File]].serializable)
  checkAll("CellDecoder[File]", SerializableTests[CellDecoder[File]].serializable)
  checkAll("RowEncoder[File]", SerializableTests[RowEncoder[File]].serializable)
  checkAll("RowDecoder[File]", SerializableTests[RowDecoder[File]].serializable)


  checkAll("CellEncoder[Float]", SerializableTests[CellEncoder[Float]].serializable)
  checkAll("CellDecoder[Float]", SerializableTests[CellDecoder[Float]].serializable)
  checkAll("RowEncoder[Float]", SerializableTests[RowEncoder[Float]].serializable)
  checkAll("RowDecoder[Float]", SerializableTests[RowDecoder[Float]].serializable)


  checkAll("CellEncoder[Int]", SerializableTests[CellEncoder[Int]].serializable)
  checkAll("CellDecoder[Int]", SerializableTests[CellDecoder[Int]].serializable)
  checkAll("RowEncoder[Int]", SerializableTests[RowEncoder[Int]].serializable)
  checkAll("RowDecoder[Int]", SerializableTests[RowDecoder[Int]].serializable)



  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(Arbitrary.arbitrary[Int]))

  checkAll("RowEncoder[List[Int]]", SerializableTests[RowEncoder[List[Int]]].serializable)
  checkAll("RowDecoder[List[Int]]", SerializableTests[RowDecoder[List[Int]]].serializable)


  checkAll("CellEncoder[Long]", SerializableTests[CellEncoder[Long]].serializable)
  checkAll("CellDecoder[Long]", SerializableTests[CellDecoder[Long]].serializable)
  checkAll("RowEncoder[Long]", SerializableTests[RowEncoder[Long]].serializable)
  checkAll("RowDecoder[Long]", SerializableTests[RowDecoder[Long]].serializable)


  checkAll("CellEncoder[Option[Int]]", SerializableTests[CellEncoder[Option[Int]]].serializable)
  checkAll("CellDecoder[Option[Int]]", SerializableTests[CellDecoder[Option[Int]]].serializable)
  checkAll("RowEncoder[Option[(Int, Int, Int)]]", SerializableTests[RowEncoder[Option[(Int, Int, Int)]]].serializable)
  checkAll("RowDecoder[Option[(Int, Int, Int)]]", SerializableTests[RowDecoder[Option[(Int, Int, Int)]]].serializable)


  checkAll("CellEncoder[Path]", SerializableTests[CellEncoder[Path]].serializable)
  checkAll("CellDecoder[Path]", SerializableTests[CellDecoder[Path]].serializable)
  checkAll("RowEncoder[Path]", SerializableTests[RowEncoder[Path]].serializable)
  checkAll("RowDecoder[Path]", SerializableTests[RowDecoder[Path]].serializable)

  implicit val arb: Arbitrary[Seq[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Seq, Int](Arbitrary.arbitrary[Int]))

  checkAll("RowEncoder[Seq[Int]]", SerializableTests[RowEncoder[Seq[Int]]].serializable)
  checkAll("RowDecoder[Seq[Int]]", SerializableTests[RowDecoder[Seq[Int]]].serializable)


  checkAll("CellEncoder[Short]", SerializableTests[CellEncoder[Short]].serializable)
  checkAll("CellDecoder[Short]", SerializableTests[CellDecoder[Short]].serializable)
  checkAll("RowEncoder[Short]", SerializableTests[RowEncoder[Short]].serializable)
  checkAll("RowDecoder[Short]", SerializableTests[RowDecoder[Short]].serializable)


  implicit val arb: Arbitrary[Stream[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Stream, Int](Arbitrary.arbitrary[Int]))

  checkAll("RowEncoder[Stream[Int]]", SerializableTests[RowEncoder[Stream[Int]]].serializable)
  checkAll("RowDecoder[Stream[Int]]", SerializableTests[RowDecoder[Stream[Int]]].serializable)


  checkAll("CellEncoder[String]", SerializableTests[CellEncoder[String]].serializable)
  checkAll("CellDecoder[String]", SerializableTests[CellDecoder[String]].serializable)
  checkAll("RowEncoder[String]", SerializableTests[RowEncoder[String]].serializable)
  checkAll("RowDecoder[String]", SerializableTests[RowDecoder[String]].serializable)


  checkAll("CellEncoder[URI]", SerializableTests[CellEncoder[URI]].serializable)
  checkAll("CellDecoder[URI]", SerializableTests[CellDecoder[URI]].serializable)
  checkAll("RowEncoder[URI]", SerializableTests[RowEncoder[URI]].serializable)
  checkAll("RowDecoder[URI]", SerializableTests[RowDecoder[URI]].serializable)


  checkAll("CellEncoder[URL]", SerializableTests[CellEncoder[URL]].serializable)
  checkAll("CellDecoder[URL]", SerializableTests[CellDecoder[URL]].serializable)
  checkAll("RowEncoder[URL]", SerializableTests[RowEncoder[URL]].serializable)
  checkAll("RowDecoder[URL]", SerializableTests[RowDecoder[URL]].serializable)


  checkAll("CellEncoder[UUID]", SerializableTests[CellEncoder[UUID]].serializable)
  checkAll("CellDecoder[UUID]", SerializableTests[CellDecoder[UUID]].serializable)
  checkAll("RowEncoder[UUID]", SerializableTests[RowEncoder[UUID]].serializable)
  checkAll("RowDecoder[UUID]", SerializableTests[RowDecoder[UUID]].serializable)


  implicit val arb: Arbitrary[Vector[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Vector, Int](Arbitrary.arbitrary[Int]))

  checkAll("RowEncoder[Vector[Int]]", SerializableTests[RowEncoder[Vector[Int]]].serializable)
  checkAll("RowDecoder[Vector[Int]]", SerializableTests[RowDecoder[Vector[Int]]].serializable)

}
