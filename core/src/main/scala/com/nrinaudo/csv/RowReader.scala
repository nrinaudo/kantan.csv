package com.nrinaudo.csv

import simulacrum.typeclass

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.ArrayBuffer

object RowReader {
  def apply[A](f: ArrayBuffer[String] => A): RowReader[A] = apply(false, f)

  private def apply[A](h: Boolean, f: ArrayBuffer[String] => A): RowReader[A] = new RowReader[A] {
      override def read(row: ArrayBuffer[String]) = f(row)
      override def hasHeader = h
  }

  /** Generic {{{RowReader}}} for collections. */
  implicit def collection[A: CellReader, M[X]](implicit cbf: CanBuildFrom[Nothing, A, M[A]]): RowReader[M[A]] = apply { ss =>
    ss.foldLeft(cbf.apply()) { (acc, s) => acc += CellReader[A].read(s) }.result()
  }

  /** Generic {{{RowReader}}} for tuples. */
  implicit def tuple[A: CellReader, B: CellReader]: RowReader[(A, B)] = apply { ss =>
    (CellReader[A].read(ss.head), CellReader[B].read(ss(1)))
  }
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
