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

import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.SerializableTests

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import scala.reflect.ClassTag

class SerializationTests extends DisciplineSuite with VersionSpecificSerializationTests {

  // - Helper methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def checkCodec[A: CellEncoder: CellDecoder: RowEncoder: RowDecoder](implicit tag: ClassTag[A]): Unit = {
    checkCellCodec[A]
    checkRowCodec[A]
  }

  def checkCellCodec[A: CellEncoder: CellDecoder](implicit tag: ClassTag[A]): Unit = {
    val label = tag.runtimeClass.getSimpleName
    checkAll(s"CellEncoder[$label]", SerializableTests[CellEncoder[A]].serializable)
    checkAll(s"CellDecoder[$label]", SerializableTests[CellDecoder[A]].serializable)
  }

  def checkRowCodec[A: RowEncoder: RowDecoder](implicit tag: ClassTag[A]): Unit = {
    val label = tag.runtimeClass.getSimpleName
    checkAll(s"RowEncoder[$label]", SerializableTests[RowEncoder[A]].serializable)
    checkAll(s"RowDecoder[$label]", SerializableTests[RowDecoder[A]].serializable)
  }

  // - Simple codecs ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  checkCodec[BigDecimal]
  checkCodec[BigInt]
  checkCodec[Boolean]
  checkCodec[Byte]
  checkCodec[Char]
  checkCodec[Double]
  checkCodec[File]
  checkCodec[Float]
  checkCodec[Int]
  checkCodec[Long]
  checkCodec[Path]
  checkCodec[Short]
  checkCodec[String]
  checkCodec[URI]
  checkCodec[URL]
  checkCodec[UUID]

  implicit val dateCodec: CellCodec[Date] =
    CellCodec.dateCodec(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH))
  checkCodec[Date]

  // - Compound codecs -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  checkCellCodec[Either[Int, Boolean]]
  checkRowCodec[Either[(Int, Int, Int), Boolean]]

  checkCellCodec[Option[Int]]
  checkRowCodec[Option[(Int, Int, Int)]]

  // - Row codecs ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  checkRowCodec[List[Int]]
  checkRowCodec[Seq[Int]]
  checkRowCodec[Vector[Int]]

}
