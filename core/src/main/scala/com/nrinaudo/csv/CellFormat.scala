package com.nrinaudo.csv

object CellFormat {
  /** Creates a {{{CellFormat}}} from an existing {{{CellReader}}} and {{{CellWriter}}}. */
  implicit def apply[A](implicit r: CellReader[A], w: CellWriter[A]): CellFormat[A] = new CellFormat[A] {
    override def read(a: String) = r.read(a)
    override def write(a: A) = w.write(a)
  }
}

/** Combines {{{CellReader}}} and {{{CellWriter}}}.
  *
  * Instance for types that already have a {{{CellReader}}} and {{{CellWriter}}} are derived automatically.
  */
trait CellFormat[A] extends CellReader[A] with CellWriter[A]
