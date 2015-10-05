package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

/** Type class used to decode the content of a single CSV cell.
  *
  * Standard types are already provided for in the companion object.
  *
  * See also the companion class, {{{CellEncoder}}}, as well as {{{RowDecoder}}}, used to read wholes rows.
  */
@typeclass trait CellDecoder[A] {
  /** Turns the content of a CSV cell into an {{{A}}}. */
  @noop def decode(s: String): DecodeResult[A]

  @noop def decode(ss: Seq[String], index: Int): DecodeResult[A] =
    if(ss.isDefinedAt(index)) decode(ss(index))
    else                      DecodeResult.DecodeFailure

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
  def apply[A](f: String => DecodeResult[A]): CellDecoder[A] = new CellDecoder[A] {
    override def decode(a: String) = f(a)
  }

  implicit val string: CellDecoder[String]     = CellDecoder(s => DecodeResult.success(s))
  implicit val char:   CellDecoder[Char]       = CellDecoder(s => if(s.length == 1) DecodeResult.success(s(0)) else DecodeResult.decodeFailure)
  implicit val int   : CellDecoder[Int]        = CellDecoder(s => DecodeResult(s.toInt))
  implicit val float : CellDecoder[Float]      = CellDecoder(s => DecodeResult(s.toFloat))
  implicit val double: CellDecoder[Double]     = CellDecoder(s => DecodeResult(s.toDouble))
  implicit val long  : CellDecoder[Long]       = CellDecoder(s => DecodeResult(s.toLong))
  implicit val short : CellDecoder[Short]      = CellDecoder(s => DecodeResult(s.toShort))
  implicit val byte  : CellDecoder[Byte]       = CellDecoder(s => DecodeResult(s.toByte))
  implicit val bool  : CellDecoder[Boolean]    = CellDecoder(s => DecodeResult(s.toBoolean))
  implicit val bigInt: CellDecoder[BigInt]     = CellDecoder(s => DecodeResult(BigInt.apply(s)))
  implicit val bigDec: CellDecoder[BigDecimal] = CellDecoder(s => DecodeResult(BigDecimal.apply(s)))

  implicit def opt[A: CellDecoder]: CellDecoder[Option[A]] = CellDecoder { s =>
    if(s.isEmpty) DecodeResult.success(None)
    else          CellDecoder[A].decode(s).map(Option.apply)
  }

  implicit def either[A: CellDecoder, B: CellDecoder]: CellDecoder[Either[A, B]] =
    CellDecoder { s => CellDecoder[A].decode(s).map(a => Left(a): Either[A, B])
      .orElse(CellDecoder[B].decode(s).map(b => Right(b): Either[A, B]))
    }
}