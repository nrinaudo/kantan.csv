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
package laws
package discipline

import org.scalacheck.Prop, Prop._

trait VersionSpecificReaderEngineTests { self: ReaderEngineTests =>
  def versionSpecificProps: Seq[(String, Prop)] = Seq(
    "withFilter"    -> forAll(laws.withFilter _),
    "toStream"      -> forAll(laws.toStream _),
    "toTraversable" -> forAll(laws.toTraversable _),
    "toIterator"    -> forAll(laws.toIterator _)
  )

}
