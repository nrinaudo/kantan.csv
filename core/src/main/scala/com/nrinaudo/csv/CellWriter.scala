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

  implicit val string: CellWriter[String]     = CellWriter(s => s)
  implicit val char  : CellWriter[Char]       = CellWriter(_.toString)
  implicit val int   : CellWriter[Int]        = CellWriter(_.toString)
  implicit val float : CellWriter[Float]      = CellWriter(_.toString)
  implicit val double: CellWriter[Double]     = CellWriter(_.toString)
  implicit val long  : CellWriter[Long]       = CellWriter(_.toString)
  implicit val short : CellWriter[Short]      = CellWriter(_.toString)
  implicit val byte  : CellWriter[Byte]       = CellWriter(_.toString)
  implicit val bool  : CellWriter[Boolean]    = CellWriter(_.toString)
  implicit val bigInt: CellWriter[BigInt]     = CellWriter(_.toString())
  implicit val bigDec: CellWriter[BigDecimal] = CellWriter(_.toString())

  implicit def opt[A: CellWriter]: CellWriter[Option[A]] = CellWriter(oa => oa.map(CellWriter[A].write).getOrElse(""))
  implicit def either[A: CellWriter, B: CellWriter]: CellWriter[Either[A, B]] = CellWriter(eab => eab match {
    case Left(a)  => a.asCsvCell
    case Right(b) => b.asCsvCell
  })
}