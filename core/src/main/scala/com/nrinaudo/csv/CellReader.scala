package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

/** Typeclass used to reader the content of a single CSV cell.
  *
  * Standard types are already provided for in the companion object.
  *
  * See also the companion class, {{{CellWriter}}}, as well as {{{RowReader}}}, used to read wholes rows.
  */
@typeclass trait CellReader[A] {
  /** Turns the content of a CSV cell into an {{{A}}}. */
  def read(a: String): A

  /** Creates a new {{{CellReader}}} that applies the specified function to the result of {{{read}}}.
    *
    * This can be useful if you just need a {{{CellReader}}} implementation that specialises an existing type - to add
    * a scalaz tag, say.
    */
  @noop def map[B](f: A => B): CellReader[B] = CellReader(str => f(read(str)))
}

/** Provides default implementations and construction methods for {{{CellReader}}}. */
object CellReader {
  /** Creates a new instance of {{{CellReader}}} that uses the specified function to parse data. */
  def apply[A](f: String => A): CellReader[A] = new CellReader[A] {
    override def read(a: String) = f(a)
  }

  implicit val string: CellReader[String]  = apply(s => s)
  implicit val int   : CellReader[Int]     = apply(_.toInt)
  implicit val float : CellReader[Float]   = apply(_.toFloat)
  implicit val double: CellReader[Double]  = apply(_.toDouble)
  implicit val long  : CellReader[Long]    = apply(_.toLong)
  implicit val short : CellReader[Short]   = apply(_.toShort)
  implicit val byte  : CellReader[Byte]    = apply(_.toByte)
  implicit val bool  : CellReader[Boolean] = apply(_.toBoolean)
  
  implicit def opt[A: CellReader]: CellReader[Option[A]] = apply { s =>
    if(s.isEmpty) None
    else          Some(CellReader[A].read(s))
  }
}