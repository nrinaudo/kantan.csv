package com.nrinaudo.csv

import simulacrum.typeclass

object RowWriter {
  def apply[A](f: A => List[String]): RowWriter[A] = apply(None, f)

  private def apply[A](header: List[String], f: A => List[String]): RowWriter[A] = apply(Some(header), f)

  private def apply[A](h: Option[List[String]], f: A => List[String]): RowWriter[A] = new RowWriter[A] {
      override def write(a: A) = f(a)
      override def header = h
    }

  implicit val list: RowWriter[List[String]] = apply(a => a)
  implicit val vector: RowWriter[Vector[String]] = apply(a => a.toList)
}

@typeclass trait RowWriter[A] { self =>
  def write(a: A): List[String]
  def header: Option[List[String]]

  def withHeader(h: String*): RowWriter[A] = RowWriter(h.toList, write _)
  def noHeader: RowWriter[A] = RowWriter(write _)
}
