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

package kantan.csv

import java.io.{Closeable, Reader}
import kantan.csv.engine.ReaderEngine

/** Iterator on CSV rows.
  *
  * Instances of [[CsvReader]] are commonly obtained through [[kantan.csv.ops.CsvInputOps.asCsvReader]]:
  * {{{
  *   import kantan.csv.ops._
  *
  *   val file: File = ??? // some CSV file.
  *   file.asCsvReader[List[Int]](',', true)
  * }}}
  *
  * [[CsvReader]] provides most of the common Scala collection operations, such as [[map]] or [[filter]]. However,
  * working with a `CsvReader` of [[ReadResult]] is a very common pattern that doesn't lend itself well to using such
  * combinators: mapping on each row would require first mapping into the [[CsvReader]], then into each [[ReadResult]],
  * which is cumbersome and not terribly clear.
  *
  * kantan.csv provides syntax for this pattern with [[kantan.csv.ops.CsvReaderOps]]: filtering into a result, for
  * example, is made easier by [[kantan.csv.ops.CsvReaderOps.filterResult]]
  * {{{
  *   import kantan.csv.ops._
  *
  *   file.asCsvReader[List[Int]](',', true).filterResult(_ % 2 == 0)
  * }}}
  */
trait CsvReader[+A] extends TraversableOnce[A] with Closeable { self ⇒
  // - Iterator-like methods -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Returns `true` if there is at least one mor row to read, `false` otherwise. */
  def hasNext: Boolean

  /** Reads the next CSV row.
    *
    * This method is not meant for internal purposes only and is not meant to be called directly. Use [[next]] instead.
    */
  protected def readNext(): A

  /** Releases any resource used by this [[CsvReader]].
    *
    * This happens automatically whenever a fatal error occurs or there is no more CSV rows to read. Applications that
    * do not read the entire stream, however, need to call [[close]] manually.
    */
  def close(): Unit

  /** Reads the next CSV row.
    *
    * This method will automatically call [[close]] if an error occurs or the end of the CSV data has been reached.
    */
  def next(): A = {
    val a = {
      try { readNext() }
      catch {
        case e: Throwable ⇒
          close()
          throw e
      }
    }
    if(!hasNext) close()
    a
  }


  // - Useful methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Discards the first `n` rows.
    *
    * @param n number of rows to discard.
    */
  def drop(n: Int): CsvReader[A] =
    if(n > 0 && hasNext) {
      next()
      drop(n - 1)
    }
    else this

  /** Discards rows while the specified predicate holds.
    *
    * The returned [[CsvReader]] will start at the first row for which `p` returned `false`.
    *
    * @param p predicate to apply to each row.
    */
  def dropWhile(p: A ⇒ Boolean): CsvReader[A] =
  // Empty rows: nothing to drop
    if(isEmpty) this
    else {
      // Looks for the first element that does not match p.
      var n = self.next()
      while(self.hasNext && p(n)) n = self.next()

      // No such element, return the empty stream.
      if(isEmpty && p(n)) this

      // We've found one such element, returns a bit of a mess of a CsvRows that'll first return it, then whatever is
      // left in the stream.
      else new CsvReader[A] {
        var done = false

        override def hasNext: Boolean = !done || self.hasNext
        override def close() = self.close()

        override def readNext(): A =
          if(done) self.readNext()
          else {
            done = true
            n
          }
      }
    }

  /** Takes the first `n` rows, discarding the remaining ones.
    *
    * @param n number of rows to take.
    */
  def take(n: Int): CsvReader[A] = new CsvReader[A] {
    var count = n
    override def hasNext: Boolean = count > 0 && self.hasNext
    override def readNext(): A = {
      if(count > 0) {
        val a = self.readNext()
        count -= 1
        a
      }
      else CsvReader.empty.next()
    }
    override def close(): Unit = self.close()
  }

  /** Applies the specified partial function to all CSV rows.
    *
    * Any row for which the function is not defined will be filtered out. All other rows will be replaced by the
    * returned value.
    *
    * This is particularly useful when dealing with [[kantan.csv.ReadResult]]: it's an easy way of skipping over all
    * failures and keeping successes only:
    * {{{
    *   val rows: CsvReader[(Int, String)] = file.asCsvReader[(Int, String)](',', true).collect {
    *     case Success(a) ⇒ a
    *   }
    * }}}
    *
    * @param f partial function to apply to each row.
    */
  def collect[B](f: PartialFunction[A, B]): CsvReader[B] = new CsvReader[B] {
    var n = self.find(f.isDefinedAt)
    override def hasNext: Boolean = n.isDefined
    override def readNext(): B = {
      val r = n.getOrElse(CsvReader.empty.next())
      n = self.find(f.isDefinedAt)
      f(r)
    }
    override def close(): Unit = self.close()
  }


  // - Monadic operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Turns a `CsvReader[A]` into a `CsvReader[B]`.
    *
    * Working with [[CsvReader]] of [[ReadResult]] is such a common pattern that kantan.csv provides syntax for mapping
    * directly into the [[ReadResult]]:
    * {{{
    *   import kantan.csv.ops._
    *
    *   someFile.asCsvReader[Person](',', true).mapResult(_.name)
    * }}}
    *
    * @param f function to apply to each row.
    */
  def map[B](f: A ⇒ B): CsvReader[B] = new CsvReader[B] {
    override def hasNext: Boolean = self.hasNext
    override def readNext(): B = f(self.readNext())
    override def close(): Unit = self.close()
  }

  /** Turns a `CsvReader[A]` into a `CsvReader[B]`.
    *
    * @param f function to apply to each row.
    */
  def flatMap[B](f: A ⇒ CsvReader[B]): CsvReader[B] = new CsvReader[B] {
    private var cur: CsvReader[B] = CsvReader.empty

    override def hasNext: Boolean = cur.hasNext || self.hasNext && { cur = f(self.next()); hasNext}
    override def readNext(): B = cur.readNext()
    override def close(): Unit = self.close()
  }

  /** Filters out all rows that validate the specified predicate.
    *
    * Working with [[CsvReader]] of [[ReadResult]] is such a common pattern that kantan.csv provides syntax for
    * filtering directly into the [[ReadResult]]:
    * {{{
    *   import kantan.csv.ops._
    *
    *   someFile.asCsvReader[Person](',', true).filterResult(_.age > 18)
    * }}}
    *
    * @param p
    * @return
    */
  def filter(p: A ⇒ Boolean): CsvReader[A] = collect {
    case a if p(a) ⇒ a
  }

  def withFilter(p: A ⇒ Boolean): CsvReader[A] = filter(p)


  // - TraversableOnce -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def foreach[U](f: A ⇒ U): Unit = while(hasNext) f(next())
  override def seq: TraversableOnce[A] = this
  override def hasDefiniteSize: Boolean = isEmpty

  override def copyToArray[B >: A](xs: Array[B], start: Int, len: Int): Unit = {
    var i = start
    val end = start + math.min(len, xs.length - start)
    while(i < end && hasNext) {
      xs(i) = next()
      i += 1
    }
  }

  override def forall(p: A ⇒ Boolean): Boolean = {
    var res = true
    while(res && hasNext) res = p(next())
    res
  }
  override def toTraversable: Traversable[A] = toStream
  override def isEmpty: Boolean = !hasNext
  override def find(p: A ⇒ Boolean): Option[A] = {
    var res: Option[A] = None
    while(res.isEmpty && hasNext) {
      val n = next()
      if(p(n)) res = Some(n)
    }
    res
  }
  override def exists(p: A ⇒ Boolean): Boolean = {
    var res = false
    while(!res && hasNext) res = p(next())
    res
  }
  override def toStream: Stream[A] = if(hasNext) Stream.cons(next(), toStream) else Stream.empty
  override def toIterator: Iterator[A] = new Iterator[A] {
    override def hasNext: Boolean = self.hasNext
    override def next(): A = self.next()
  }
  override def isTraversableAgain: Boolean = false
}

/** Provides instance creation and summoning methods. */
object CsvReader {
  /** Creates an empty [[kantan.csv.CsvReader]]. */
  val empty: CsvReader[Nothing] = new CsvReader[Nothing] {
    override def hasNext: Boolean = false
    override protected def readNext(): Nothing = throw new NoSuchElementException("next on empty CSV rows")
    override def close(): Unit = ()
  }

  /** Creates a [[CsvReader]] with a single row. */
  def singleton[A](a: A): CsvReader[A] = fromSafe(a)(a ⇒ Seq(a).iterator)(_ ⇒ ())

  /** Creates a new instance of [[CsvReader]].
    *
    * This method works with the assumption that the specified iterator is safe - that calling `next` when `hasNext`
    * has returned `true` cannot throw an exception. For unsafe iterators, such as IO-bound ones, use [[fromUnsafe]]
    * instead.
    *
    * @param in where to read data from
    * @param open function that turns `in` into an iterator.
    * @param release function that closes `in` when no longer needed.
    */
  def fromSafe[I, R](in: I)(open: I ⇒ Iterator[R])(release: I ⇒ Unit): CsvReader[R] = new CsvReader[R] {
    val it = open(in)
    override protected def readNext() = it.next()
    override def hasNext = it.hasNext
    override def close() = release(in)
  }

  /** Creates a new instance of [[CsvReader]].
    *
    * This method works with the assumption that the specified iterator is unsafe - that calling `next` when `hasNext`
    * has returned `true` might throw an exception. For safe iterators, use [[fromSafe]] instead.
    *
    * @param in where to read data from
    * @param open function that turns `in` into an iterator.
    * @param release function that closes `in` when no longer needed.
    */
  def fromUnsafe[I, R](in: I)(open: I ⇒ Iterator[R])(release: I ⇒ Unit): CsvReader[ParseResult[R]] =
    new CsvReader[ParseResult[R]] {
      val it = open(in)
      override def hasNext = it.hasNext
      override protected def readNext() =
        if(it.hasNext) ParseResult(it.next())
        else           ParseResult.noSuchElement
      override def close() = release(in)
    }


  /** Opens a [[CsvReader]] on the specified `Reader`.
    *
    * @param reader what to parse as CSV
    * @param sep column separator
    * @param header whether or not to skip the first row
    */
  def apply[A](reader: Reader, sep: Char, header: Boolean)
              (implicit da: RowDecoder[A], e: ReaderEngine): CsvReader[ReadResult[A]] = {
    val data: CsvReader[ReadResult[Seq[String]]] = e.readerFor(reader, sep)

    if(header && data.hasNext) data.next()

    data.map(_.flatMap(da.decode))
  }
}
