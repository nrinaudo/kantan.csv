package kantan.csv

import java.io.StringWriter
import kantan.csv.engine.{ReaderEngine, WriterEngine}
import scala.collection.generic.CanBuildFrom

object ops {
  implicit class CsvOutputOps[A](val a: A) extends AnyVal {
    def asCsvWriter[B: RowEncoder](sep: Char, header: Seq[String] = Seq.empty)(implicit oa: CsvOutput[A], e: WriterEngine): CsvWriter[B] =
      oa.writer(a, sep, header)

    def writeCsv[B: RowEncoder](rows: TraversableOnce[B], sep: Char, header: Seq[String] = Seq.empty)(implicit oa: CsvOutput[A], e: WriterEngine): Unit =
      oa.write(a, rows, sep, header)
  }

  /** Provides useful syntax for types that have implicit instances of [[CsvInput]] in scope.
    *
    * The most common use case is to turn a value into a [[CsvReader]] through [[asCsvReader]]:
    * {{{
    *   val f: java.io.File = ???
    *   f.asCsvReader[List[Int]](',', true)
    * }}}
    *
    * A slightly less common use case is to load an entire CSV file in memory through [[readCsv]]:
    * {{{
    *   val f: java.io.File = ???
    *   f.readCsv[List, List[Int]](',', true)
    * }}}
    *
    * Unsafe versions of these methods are also available, even if usually advised against.
    */
  implicit class CsvInputOps[A](val a: A) extends AnyVal {
    /** Shorthand for [[CsvInput!.reader CsvInput.reader]]. */
    def asCsvReader[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], e: ReaderEngine): CsvReader[CsvResult[B]] =
      ai.reader[B](a, sep, header)

    /** Shorthand for [[CsvInput.unsafeReader]]. */
    def asUnsafeCsvReader[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], e: ReaderEngine): CsvReader[B] =
      ai.unsafeReader[B](a, sep, header)

    /** Shorthand for [[CsvInput.read]]. */
    def readCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], cbf: CanBuildFrom[Nothing, CsvResult[B], C[CsvResult[B]]], e: ReaderEngine) =
      ai.read[C, B](a, sep, header)

    /** Shorthand for [[CsvInput.unsafeRead]]. */
    def unsafeReadCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvInput[A], cbf: CanBuildFrom[Nothing, B, C[B]], e: ReaderEngine) =
      ai.unsafeRead[C, B](a, sep, header)
  }

  implicit class TraversableOnceOps[A](val rows: TraversableOnce[A]) extends AnyVal {
    def asCsv(sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine, ae: RowEncoder[A]): String = {
      val out = new StringWriter()
      CsvWriter(out, sep, header).write(rows).close()
      out.toString
    }
  }

  // Alright, yes, this is nasty. There are abstractions designed to deal with just this situation, but not everyone
  // knows about them / understands them / can afford to depend on libraries that provide them.
  /** Provides useful syntax for `CsvReader[CsvResult[A]]`.
    *
    * When parsing CSV data, a very common scenario is to get an instance of [[CsvReader]] and then use common
    * combinators such as `map` and `flatMap` on it. This can be awkward when the actual interesting value is
    * itself within a [[CsvResult]] which also needs to be mapped into. [[CsvReaderOps]] provides shortcuts, such as:
    * {{{
    *   val reader: CsvReader[CsvResult[List[Int]]] = ???
    *
    *   // Not the most useful code in the world, but shows how one can map and filter directly on the nested value.
    *   reader.mapResult(_.sum).filterResult(_ % 2 == 0)
    * }}}
    */
  implicit class CsvReaderOps[A](val results: CsvReader[CsvResult[A]]) extends AnyVal {
    /** Turns a `CsvReader[CsvResult[A]]` into a `CsvReader[CsvResult[B]]`. */
    def mapResult[B](f: A ⇒ B): CsvReader[CsvResult[B]] = results.map(_.map(f))

    /** Turns a `CsvReader[CsvResult[A]]` into a `CsvReader[CsvResult[B]]`. */
    def flatMapResult[B](f: A ⇒ CsvResult[B]): CsvReader[CsvResult[B]] = results.map(_.flatMap(f))

    /** Filters on all successfull values that match the specified predicate. */
    def filterResult(f: A ⇒ Boolean): CsvReader[CsvResult[A]] = results.filter(_.exists(f))
  }
}
