package com.nrinaudo.csv

import simulacrum.typeclass

import scala.collection.mutable.ArrayBuffer

object RowReader {
  def apply[A](f: ArrayBuffer[String] => A): RowReader[A] = apply(false, f)

  private def apply[A](h: Boolean, f: ArrayBuffer[String] => A): RowReader[A] = new RowReader[A] {
      override def read(row: ArrayBuffer[String]): A = f(row)
      override def hasHeader: Boolean = h
  }

  /** Used to turn each CSV row from a mutable array to an immutable vector. */
  implicit val vector: RowReader[Vector[String]] = apply(_.toVector)

  /** Used to turn each CSV row from a mutable array to an immutable list. */
  implicit val list: RowReader[List[String]] = apply(_.toList)
}

/** Typeclass for reading the content of a CSV row.
  *
  * Default implementations are provided in the companion object.
  */
@typeclass trait RowReader[A] { self =>
  def read(row: ArrayBuffer[String]): A
  def hasHeader: Boolean = false
  def withHeader: RowReader[A] = RowReader(true, read _)
  def noHeader: RowReader[A] = RowReader(false, read _)
}
