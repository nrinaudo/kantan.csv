package com.nrinaudo.csv

import simulacrum.{op, noop, typeclass}

import scala.util.Try

/** Typeclass used to reader the content of a single CSV cell.
  *
  * Standard types are already provided for in the companion object.
  *
  * See also the companion class, {{{CellWriter}}}, as well as {{{RowReader}}}, used to read wholes rows.
  */
@typeclass trait CellReader[A] {
  /** Turns the content of a CSV cell into an {{{A}}}. */
  @noop def read(a: String): A

  /** Creates a new {{{CellReader}}} that applies the specified function to the result of {{{read}}}.
    *
    * This can be useful if you just need a {{{CellReader}}} implementation that specialises an existing type - to add
    * a scalaz tag, say.
    */
  @noop def map[B](f: A => B): CellReader[B] = CellReader((read _) andThen f)
}

/** Provides default implementations and construction methods for {{{CellReader}}}. */
object CellReader {
  /** Creates a new instance of {{{CellReader}}} that uses the specified function to parse data. */
  def apply[A](f: String => A): CellReader[A] = new CellReader[A] {
    override def read(a: String) = f(a)
  }

  implicit val string: CellReader[String]     = CellReader(s => s)
  implicit val char:   CellReader[Char]       = CellReader(s => if(s.length == 1) s(0) else throw new Exception(s"Not a valid char: $s"))
  implicit val int   : CellReader[Int]        = CellReader(_.toInt)
  implicit val float : CellReader[Float]      = CellReader(_.toFloat)
  implicit val double: CellReader[Double]     = CellReader(_.toDouble)
  implicit val long  : CellReader[Long]       = CellReader(_.toLong)
  implicit val short : CellReader[Short]      = CellReader(_.toShort)
  implicit val byte  : CellReader[Byte]       = CellReader(_.toByte)
  implicit val bool  : CellReader[Boolean]    = CellReader(_.toBoolean)
  implicit val bigInt: CellReader[BigInt]     = CellReader(s => BigInt.apply(s))
  implicit val bigDec: CellReader[BigDecimal] = CellReader(s => BigDecimal.apply(s))

  implicit def opt[A: CellReader]: CellReader[Option[A]] = CellReader { s =>
    if(s.isEmpty) None
    else          Some(CellReader[A].read(s))
  }

  implicit def either[A: CellReader, B: CellReader]: CellReader[Either[A, B]] =
    CellReader(s => Try(Left(CellReader[A].read(s))).getOrElse(Right(CellReader[B].read(s))))
}