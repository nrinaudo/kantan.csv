package com.nrinaudo.csv

object CellFormat {
  implicit def apply[A](implicit r: CellReader[A], w: CellWriter[A]): CellFormat[A] = new CellFormat[A] {
    override def read(a: String) = r.read(a)
    override def write(a: A) = w.write(a)
  }
}

trait CellFormat[A] extends CellReader[A] with CellWriter[A]
