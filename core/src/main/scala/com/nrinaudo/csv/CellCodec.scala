package com.nrinaudo.csv

/** Combines {{{CellReader}}} and {{{CellWriter}}}.
  *
  * Instance for types that already have a {{{CellReader}}} and {{{CellWriter}}} are derived automatically.
  */
trait CellCodec[A] extends CellDecoder[A] with CellEncoder[A]

object CellCodec {
  /** Creates a {{{CellFormat}}} from an existing {{{CellReader}}} and {{{CellWriter}}}. */
  implicit def apply[A](implicit r: CellDecoder[A], w: CellEncoder[A]): CellCodec[A] =
    CellCodec(s => r.decode(s), a => w.encode(a))

  def apply[A](reader: String => Option[A], writer: A => String): CellCodec[A] = new CellCodec[A] {
    override def decode(a: String) = reader(a)
    override def encode(a: A) = writer(a)
  }
}

