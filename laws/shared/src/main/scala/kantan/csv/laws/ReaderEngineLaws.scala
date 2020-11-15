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

package kantan.csv.laws

import kantan.csv.engine.ReaderEngine
import kantan.csv.ops._
import kantan.csv.rfc
import org.scalacheck.Prop.throws

trait ReaderEngineLaws
    extends RfcReaderLaws with SpectrumReaderLaws with KnownFormatsReaderLaws with VersionSpecificReaderEngineLaws {
  protected def asReader(csv: List[List[Cell]]): kantan.csv.CsvReader[List[Cell]] =
    csv.asCsv(rfc).asUnsafeCsvReader[List[Cell]](rfc)

  def nextOnEmpty(csv: List[List[Cell]]): Boolean = {
    val data = asReader(csv)

    csv.indices.foreach(_ => data.next())
    throws(classOf[java.util.NoSuchElementException])(data.next())
  }

  def nextOnEmptyTake(csv: List[List[Cell]], i: Int): Boolean = {
    val data = asReader(csv).take(i)

    csv.take(i).indices.foreach(_ => data.next())
    throws(classOf[java.util.NoSuchElementException])(data.next())
  }

  def drop(csv: List[List[Cell]], i: Int): Boolean =
    asReader(csv).drop(i).toList == csv.drop(i)

  def dropWhile(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).dropWhile(f).toList == csv.dropWhile(f)

  def take(csv: List[List[Cell]], i: Int): Boolean =
    asReader(csv).take(i).toList == csv.take(i)

  def forall(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).forall(f) == csv.forall(f)

  def find(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).find(f) == csv.find(f)

  def exists(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).exists(f) == csv.exists(f)

  def filter(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).filter(f).toList == csv.filter(f)

  def isTraversableAgain(csv: List[List[Cell]]): Boolean =
    !asReader(csv).isTraversableAgain

  def hasDefiniteSize(csv: List[List[Cell]]): Boolean = {
    def loop[A](data: kantan.csv.CsvReader[A]): Boolean =
      if(data.hasNext) !data.hasDefiniteSize && { data.next(); loop(data) }
      else data.hasDefiniteSize

    loop(asReader(csv))
  }

  def isEmpty(csv: List[List[Cell]]): Boolean = {
    def loop[A](data: kantan.csv.CsvReader[A]): Boolean =
      if(data.hasNext) !data.isEmpty && { data.next(); loop(data) }
      else data.isEmpty

    loop(asReader(csv))
  }

  def map(csv: List[List[Cell]], f: List[Cell] => Int): Boolean = asReader(csv).map(f).toList == csv.map(f)

  def flatMap(csv: List[List[Cell]], f: List[Cell] => List[List[Cell]]): Boolean =
    asReader(csv).flatMap(r => asReader(f(r))).toList == csv.flatMap(f)
}

object ReaderEngineLaws {
  def apply(e: ReaderEngine): ReaderEngineLaws = new ReaderEngineLaws {
    override implicit val engine: ReaderEngine = e
  }
}
