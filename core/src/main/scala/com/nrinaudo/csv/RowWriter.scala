package com.nrinaudo.csv

import simulacrum.typeclass

object RowWriter {
  def apply[A](f: A => List[String]): RowWriter[A] = new RowWriter[A] {
    override def write(a: A) = f(a)
    override def header = None
  }

  implicit val list: RowWriter[List[String]] = apply(a => a)
  implicit val vector: RowWriter[Vector[String]] = apply(a => a.toList)
}

@typeclass trait RowWriter[A] {
  def write(a: A): List[String]
  def header: Option[List[String]]
}
