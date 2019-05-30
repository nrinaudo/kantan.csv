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

/** Provides support for using header values rather than row indexes for decoding.
  *
  * When decoding CSV data, if the [[CsvConfiguration]] indicates the presence of a header, it will be passed to
  * [[fromHeader]], and the resulting [[RowDecoder]] will be used.
  *
  * The default behaviour is always to rely on indexes (that is, any instance of [[RowDecoder]] that might be
  * available), but you can create more useful [[HeaderDecoder]] instances through the
  * [[HeaderDecoder$ companion object]].
  */
trait HeaderDecoder[A] extends Serializable {
  def fromHeader(header: Seq[String]): DecodeResult[RowDecoder[A]]
  def noHeader: RowDecoder[A]
}

/** Provides instance summoning and creation methods for [[HeaderDecoder]]. */
object HeaderDecoder extends GeneratedHeaderDecoders {

  /** Summons an implicit instance of [[HeaderDecoder]] if one can be found, fails compilation otherwise. */
  def apply[A](implicit ev: HeaderDecoder[A]): HeaderDecoder[A] = macro imp.summon[HeaderDecoder[A]]

  private[csv] def determineRowMappings(requiredHeader: Seq[String], csvHeader: Seq[String]): DecodeResult[Seq[Int]] =
    requiredHeader.foldLeft((List.empty[String], List.empty[Int])) {
      case ((missing, found), header) =>
        val index = csvHeader.indexOf(header)

        if(index < 0) (header :: missing, found)
        else (missing, index :: found)
    } match {
      case (missing, _) if missing.nonEmpty =>
        DecodeResult.typeError(s"Missing header(s): ${missing.reverse.mkString(", ")}")
      case (_, mapping) => DecodeResult.success(mapping.reverse)
    }

  /** When no [[HeaderDecoder]] is available, fallback on whatever instance of [[RowDecoder]] is in scope. */
  implicit def defaultHeaderDecoder[A: RowDecoder]: HeaderDecoder[A] = new HeaderDecoder[A] {
    override def noHeader                        = RowDecoder[A]
    override def fromHeader(header: Seq[String]) = Right(RowDecoder[A])
  }
}
