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
  @noop def read(s: String): Option[A]

  /** Creates a new {{{CellReader}}} that applies the specified function to the result of {{{read}}}.
    *
    * This can be useful if you just need a {{{CellReader}}} implementation that specialises an existing type - to add
    * a scalaz tag, say.
    */
  @noop def map[B](f: A => B): CellReader[B] = CellReader(s => read(s).map(f))
}

/** Provides default implementations and construction methods for {{{CellReader}}}. */
object CellReader {
  /** Creates a new instance of {{{CellReader}}} that uses the specified function to parse data. */
  def apply[A](f: String => Option[A]): CellReader[A] = new CellReader[A] {
    override def read(a: String) = f(a)
  }

  @inline private def toOpt[A](a: => A): Option[A] =
    try { Some(a) }
  catch { case _: Exception => None }

  implicit val string: CellReader[String]     = CellReader(s => Option(s))
  implicit val char:   CellReader[Char]       = CellReader(s => if(s.length == 1) Option(s(0)) else None)
  implicit val int   : CellReader[Int]        = CellReader(s => toOpt(s.toInt))
  implicit val float : CellReader[Float]      = CellReader(s => toOpt(s.toFloat))
  implicit val double: CellReader[Double]     = CellReader(s => toOpt(s.toDouble))
  implicit val long  : CellReader[Long]       = CellReader(s => toOpt(s.toLong))
  implicit val short : CellReader[Short]      = CellReader(s => toOpt(s.toShort))
  implicit val byte  : CellReader[Byte]       = CellReader(s => toOpt(s.toByte))
  implicit val bool  : CellReader[Boolean]    = CellReader(s => toOpt(s.toBoolean))
  implicit val bigInt: CellReader[BigInt]     = CellReader(s => toOpt(BigInt.apply(s)))
  implicit val bigDec: CellReader[BigDecimal] = CellReader(s => toOpt(BigDecimal.apply(s)))

  implicit def opt[A: CellReader]: CellReader[Option[A]] = CellReader { s =>
    if(s.isEmpty) Some(None)
    else          CellReader[A].read(s).map(Option.apply)
  }

  implicit def either[A: CellReader, B: CellReader]: CellReader[Either[A, B]] =
    CellReader { s => CellReader[A].read(s).map(a => Left(a): Either[A, B])
      .orElse(CellReader[B].read(s).map(b => Right(b): Either[A, B]))
    }
}