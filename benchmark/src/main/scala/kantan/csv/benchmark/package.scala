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

package object benchmark {
  type CsvEntry = (Int, String, Boolean, Float)

  val rawData: List[CsvEntry] = (0x20 to 0x7e).toList.map { i =>
    if(i % 2 == 0) (i, s"Character '${i.toChar.toString}' has code point: '${i.toString}'", true, i / 100F)
    else (i, "Character \"" + i.toChar.toString + "\"\nhas code point \r\n" + i.toString, true, i / 100F)
  }

  val strData: String = rawData.asCsv(rfc)
}
