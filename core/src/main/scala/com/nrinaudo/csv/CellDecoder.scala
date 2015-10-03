package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

/** Typeclass used to decode the content of a single CSV cell.
  *
  * Standard types are already provided for in the companion object.
  *
  * See also the companion class, {{{CellEncoder}}}, as well as {{{RowDecoder}}}, used to read wholes rows.
  */
@typeclass trait CellDecoder[A] {
  /** Turns the content of a CSV cell into an {{{A}}}. */
  @noop def decode(s: String): Option[A]

  @noop def decode(ss: Seq[String], index: Int): Option[A] =
    if(ss.isDefinedAt(index)) decode(ss(index))
    else                      None

  /** Creates a new {{{CellDecoder}}} that applies the specified function to the result of {{{read}}}.
    *
    * This can be useful if you just need a {{{CellDecoder}}} implementation that specialises an existing type - to add
    * a scalaz tag, say.
    */
  @noop def map[B](f: A => B): CellDecoder[B] = CellDecoder(s => decode(s).map(f))
}

/** Provides default implementations and construction methods for {{{CellDecoder}}}. */
object CellDecoder {
  /** Creates a new instance of {{{CellDecoder}}} that uses the specified function to parse data. */
  def apply[A](f: String => Option[A]): CellDecoder[A] = new CellDecoder[A] {
    override def decode(a: String) =
      try { f(a) }
      catch { case _: Exception => None }
  }

  implicit val string: CellDecoder[String]     = CellDecoder(s => Option(s))
  implicit val char:   CellDecoder[Char]       = CellDecoder(s => if(s.length == 1) Option(s(0)) else None)
  implicit val int   : CellDecoder[Int]        = CellDecoder(s => Option(s.toInt))
  implicit val float : CellDecoder[Float]      = CellDecoder(s => Option(s.toFloat))
  implicit val double: CellDecoder[Double]     = CellDecoder(s => Option(s.toDouble))
  implicit val long  : CellDecoder[Long]       = CellDecoder(s => Option(s.toLong))
  implicit val short : CellDecoder[Short]      = CellDecoder(s => Option(s.toShort))
  implicit val byte  : CellDecoder[Byte]       = CellDecoder(s => Option(s.toByte))
  implicit val bool  : CellDecoder[Boolean]    = CellDecoder(s => Option(s.toBoolean))
  implicit val bigInt: CellDecoder[BigInt]     = CellDecoder(s => Option(BigInt.apply(s)))
  implicit val bigDec: CellDecoder[BigDecimal] = CellDecoder(s => Option(BigDecimal.apply(s)))

  implicit def opt[A: CellDecoder]: CellDecoder[Option[A]] = CellDecoder { s =>
    if(s.isEmpty) Some(None)
    else          CellDecoder[A].decode(s).map(Option.apply)
  }

  implicit def either[A: CellDecoder, B: CellDecoder]: CellDecoder[Either[A, B]] =
    CellDecoder { s => CellDecoder[A].decode(s).map(a => Left(a): Either[A, B])
      .orElse(CellDecoder[B].decode(s).map(b => Right(b): Either[A, B]))
    }
}