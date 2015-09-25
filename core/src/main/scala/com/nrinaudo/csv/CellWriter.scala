package com.nrinaudo.csv

import simulacrum.typeclass

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
}

@typeclass trait CellWriter[A] {
  def write(a: A): String
}
