package com.nrinaudo.csv

import simulacrum.{op, noop, typeclass}

@typeclass trait CellWriter[A] {
  @op("asCsvCell") def write(a: A): String

  @noop def contramap[B](f: B => A): CellWriter[B] = CellWriter(f andThen write _)
}

object CellWriter {
  import ops._

  def apply[A](f: A => String): CellWriter[A] = new CellWriter[A] {
    override def write(a: A) = f(a)
  }

  implicit val string: CellWriter[String]     = apply(s => s)
  implicit val char  : CellWriter[Char]       = apply(_.toString)
  implicit val int   : CellWriter[Int]        = apply(_.toString)
  implicit val float : CellWriter[Float]      = apply(_.toString)
  implicit val double: CellWriter[Double]     = apply(_.toString)
  implicit val long  : CellWriter[Long]       = apply(_.toString)
  implicit val short : CellWriter[Short]      = apply(_.toString)
  implicit val byte  : CellWriter[Byte]       = apply(_.toString)
  implicit val bool  : CellWriter[Boolean]    = apply(_.toString)
  implicit val bigInt: CellWriter[BigInt]     = apply(_.toString())
  implicit val bigDec: CellWriter[BigDecimal] = apply(_.toString())

  implicit def opt[A: CellWriter]: CellWriter[Option[A]] = apply(oa => oa.map(CellWriter[A].write).getOrElse(""))
  implicit def either[A: CellWriter, B: CellWriter]: CellWriter[Either[A, B]] = apply(eab => eab match {
    case Left(a)  => a.asCsvCell
    case Right(b) => b.asCsvCell
  })
}