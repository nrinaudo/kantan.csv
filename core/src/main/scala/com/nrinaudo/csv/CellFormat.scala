package com.nrinaudo.csv

/** Combines {{{CellReader}}} and {{{CellWriter}}}.
  *
  * Instance for types that already have a {{{CellReader}}} and {{{CellWriter}}} are derived automatically.
  */
trait CellFormat[A] extends CellReader[A] with CellWriter[A]

object CellFormat {
  /** Creates a {{{CellFormat}}} from an existing {{{CellReader}}} and {{{CellWriter}}}. */
  implicit def apply[A](implicit r: CellReader[A], w: CellWriter[A]): CellFormat[A] = new CellFormat[A] {
    override def read(a: String) = r.read(a)
    override def write(a: A) = w.write(a)
  }

  def apply[A](reader: String => A, writer: A => String): CellFormat[A] = new CellFormat[A] {
    override def read(a: String) = reader(a)
    override def write(a: A) = writer(a)
  }
}

