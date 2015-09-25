package com.nrinaudo.csv

import simulacrum.typeclass

object CellReader {
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
}

@typeclass trait CellReader[A] {
  def read(a: String): A
}
