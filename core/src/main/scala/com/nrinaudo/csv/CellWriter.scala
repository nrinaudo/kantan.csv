package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

@typeclass trait CellWriter[A] {
  def write(a: A): String

  @noop def contramap[B](f: B => A): CellWriter[B] = CellWriter(f andThen write _)
}

object CellWriter {
  def apply[A](f: A => String): CellWriter[A] = new CellWriter[A] {
    override def write(a: A) = f(a)
  }

  implicit val string: CellWriter[String]  = apply(s => s)
  implicit val int   : CellWriter[Int]     = apply(_.toString)
  implicit val float : CellWriter[Float]   = apply(_.toString)
  implicit val double: CellWriter[Double]  = apply(_.toString)
  implicit val long  : CellWriter[Long]    = apply(_.toString)
  implicit val short : CellWriter[Short]   = apply(_.toString)
  implicit val byte  : CellWriter[Byte]    = apply(_.toString)
  implicit val bool  : CellWriter[Boolean] = apply(_.toString)

  implicit def opt[A: CellWriter]: CellWriter[Option[A]] = apply(oa => oa.map(CellWriter[A].write).getOrElse(""))
}