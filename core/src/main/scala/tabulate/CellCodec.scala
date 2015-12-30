package tabulate

/** Combines [[CellDecoder]] and [[CellEncoder]].
  *
  * Instance for types that already have a [[CellDecoder]] and [[CellEncoder]] are derived automatically.
  */
trait CellCodec[A] extends CellDecoder[A] with CellEncoder[A]

/** Declares helpful methods for [[CellCodec]] creation. */
@export.exports(Subclass)
object CellCodec {
  /** Creates a [[tabulate.CellCodec]] from an existing [[CellDecoder]] and [[CellEncoder]]. */
  implicit def combine[A](implicit r: CellDecoder[A], w: CellEncoder[A]): CellCodec[A] =
    CellCodec(s => r.decode(s), a => w.encode(a))

  /** Creates a new [[CellCodec]] from the specified decoding and encoding functions. */
  def apply[A](decoder: String => DecodeResult[A], encoder: A => String): CellCodec[A] = new CellCodec[A] {
    override def decode(a: String) = decoder(a)
    override def encode(a: A) = encoder(a)
  }
}