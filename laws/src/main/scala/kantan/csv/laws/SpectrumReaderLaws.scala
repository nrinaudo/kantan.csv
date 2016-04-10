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

package kantan.csv.laws

import kantan.csv.engine.ReaderEngine
import kantan.csv.ops._

/** Laws based on [[https://github.com/maxogden/csv-spectrum csv-pectrum]]. */
trait SpectrumReaderLaws {
  implicit def engine: ReaderEngine

  private def equals(input: String, expected: List[List[String]]): Boolean =
    input.unsafeReadCsv[List, List[String]](',', false) == expected

  def commaInQuotes: Boolean = equals("John,Doe,120 any st.,\"Anytown, WW\",08123",
    List(List("John", "Doe", "120 any st.", "Anytown, WW", "08123")))

  def empty: Boolean = equals("1,\"\",\"\"\n2,3,4", List(
    List("1", "", ""),
    List("2", "3", "4")
  ))

  def emptyCRLF: Boolean = equals("1,\"\",\"\"\r\n2,3,4", List(
    List("1", "", ""),
    List("2", "3", "4")
  ))

  def escapedQuotes: Boolean = equals("1,\"ha \"\"ha\"\" ha\"\n3,4\n", List(
    List("1", "ha \"ha\" ha"),
    List("3", "4")
  ))

  def json: Boolean = equals("1,\"{\"\"type\"\": \"\"Point\"\", \"\"coordinates\"\": [102.0, 0.5]}\"\n", List(
    List("1", "{\"type\": \"Point\", \"coordinates\": [102.0, 0.5]}")
  ))

  def newLines: Boolean = equals("1,2,3\n\"Once upon \na time\",5,6\n7,8,9\n", List(
    List("1", "2", "3"),
    List("Once upon \na time", "5", "6"),
    List("7", "8", "9")
  ))

  def newLinesCRLF: Boolean = equals("1,2,3\r\n\"Once upon \r\na time\",5,6\r\n7,8,9\r\n", List(
      List("1", "2", "3"),
      List("Once upon \r\na time", "5", "6"),
      List("7", "8", "9")
    ))

  def quotesAndNewLines: Boolean = equals("1,\"ha \n\"\"ha\"\" \nha\"\n3,4\n", List(
    List("1", "ha \n\"ha\" \nha"),
    List("3", "4")
  ))

  def simple: Boolean = equals("1,2,3\n", List(List("1", "2", "3")))

  def simpleCRLF: Boolean = equals("1,2,3\r\n", List(List("1", "2", "3")))

  def utf8: Boolean = equals("1,2,3\n4,5,ʤ", List(
    List("1", "2", "3"),
    List("4", "5", "ʤ")
  ))
}
