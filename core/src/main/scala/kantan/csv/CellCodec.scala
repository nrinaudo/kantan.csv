package kantan.csv

import kantan.codecs.Codec

/** Combines [[CellDecoder]] and [[CellEncoder]].
  *
  * Instance for types that already have a [[CellDecoder]] and [[CellEncoder]] are derived automatically.
  */
trait CellCodec[A] extends Codec[String, A, CsvError, CellDecoder, CellEncoder] with CellDecoder[A] with CellEncoder[A]

/** Declares helpful methods for [[CellCodec]] creation. */
@export.exports(Subclass)
object CellCodec {
  /** Creates a new [[CellCodec]] from the specified decoding and encoding functions. */
  def apply[A](decoder: String ⇒ CsvResult[A], encoder: A ⇒ String): CellCodec[A] = new CellCodec[A] {
    override def decode(a: String) = decoder(a)
    override def encode(a: A) = encoder(a)
  }
}