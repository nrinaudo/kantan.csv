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

trait HeaderEncoder[A] extends Serializable {
  def header: Option[Seq[String]]
  def rowEncoder: RowEncoder[A]
}

object HeaderEncoder extends GeneratedHeaderEncoders {
  def apply[A](implicit ev: HeaderEncoder[A]): HeaderEncoder[A] =
    macro imp.summon[HeaderEncoder[A]]

  implicit def defaultHeaderEncoder[A: RowEncoder]: HeaderEncoder[A] =
    new HeaderEncoder[A] {
      override val header     = None
      override val rowEncoder = RowEncoder[A]
    }
}
