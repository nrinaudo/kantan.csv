package com.nrinaudo.tabulate

import simulacrum.{op, noop, typeclass}

@typeclass trait CellEncoder[A] {
  @op("asCsvCell") def encode(a: A): String

  @noop def contramap[B](f: B => A): CellEncoder[B] = CellEncoder(f andThen encode _)
}

object CellEncoder {
  import ops._

  def apply[A](f: A => String): CellEncoder[A] = new CellEncoder[A] {
    override def encode(a: A) = f(a)
  }

  implicit val string: CellEncoder[String]     = CellEncoder(s => s)
  implicit val char  : CellEncoder[Char]       = CellEncoder(_.toString)
  implicit val int   : CellEncoder[Int]        = CellEncoder(_.toString)
  implicit val float : CellEncoder[Float]      = CellEncoder(_.toString)
  implicit val double: CellEncoder[Double]     = CellEncoder(_.toString)
  implicit val long  : CellEncoder[Long]       = CellEncoder(_.toString)
  implicit val short : CellEncoder[Short]      = CellEncoder(_.toString)
  implicit val byte  : CellEncoder[Byte]       = CellEncoder(_.toString)
  implicit val bool  : CellEncoder[Boolean]    = CellEncoder(_.toString)
  implicit val bigInt: CellEncoder[BigInt]     = CellEncoder(_.toString())
  implicit val bigDec: CellEncoder[BigDecimal] = CellEncoder(_.toString())

  implicit def opt[A: CellEncoder]: CellEncoder[Option[A]] = CellEncoder(oa => oa.map(CellEncoder[A].encode).getOrElse(""))
  implicit def either[A: CellEncoder, B: CellEncoder]: CellEncoder[Either[A, B]] = CellEncoder(eab => eab match {
    case Left(a)  => a.asCsvCell
    case Right(b) => b.asCsvCell
  })
}