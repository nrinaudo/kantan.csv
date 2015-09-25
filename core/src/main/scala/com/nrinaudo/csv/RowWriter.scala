package com.nrinaudo.csv

import simulacrum.typeclass

object RowWriter {
  def apply[A](f: A => Seq[String]): RowWriter[A] = apply(None, f)

  private def apply[A](header: Seq[String], f: A => Seq[String]): RowWriter[A] = apply(Some(header), f)

  private def apply[A](h: Option[Seq[String]], f: A => Seq[String]): RowWriter[A] = new RowWriter[A] {
      override def write(a: A) = f(a)
      override def header = h
    }

  /** Specialised writer for sequences of strings: these do not need to be modified. */
  implicit def strSeq[M[X] <: Seq[X]]: RowWriter[M[String]] = apply { ss => ss }

  implicit def traversable[A: CellWriter, M[X] <: TraversableOnce[X]]: RowWriter[M[A]] = apply { ss =>
    ss.foldLeft(Seq.newBuilder[String]) { (acc, s) => acc += CellWriter[A].write(s) }.result()
  }

  implicit def tuple[A: CellWriter, B: CellWriter]: RowWriter[(A, B)] = apply { ab =>
    Seq(CellWriter[A].write(ab._1), CellWriter[B].write(ab._2))}
}

@typeclass trait RowWriter[A] { self =>
  def write(a: A): Seq[String]
  def header: Option[Seq[String]]

  def withHeader(h: String*): RowWriter[A] = RowWriter(h.toList, write _)
  def noHeader: RowWriter[A] = RowWriter(write _)
}
