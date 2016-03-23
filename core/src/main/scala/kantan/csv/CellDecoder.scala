package kantan.csv

import kantan.codecs.Decoder
import kantan.codecs.strings._
import kantan.csv.DecodeError.TypeError

/** Provides useful methods for summoning and creating instances of [[CellDecoder]]. */
object CellDecoder {
  /** Summons an instance of [[CellDecoder]] if an implicit one can be found in scope.
    *
    * This is essentially a shorter way of calling `implicitly[CellDecoder[A]]`.
    */
  def apply[A](implicit da: CellDecoder[A]): CellDecoder[A] = da

  /** Creates a new instance of [[CellDecoder]] that uses the specified function to decode data. */
  def apply[A](f: String ⇒ DecodeResult[A]): CellDecoder[A] = Decoder(f)
}

/** All default [[CellDecoder]] instances. */
trait CellDecoderInstances {
  /** Turns existing `StringDecoder` instances into [[CellDecoder]] ones. */
  implicit def fromStringDecoder[A](implicit da: StringDecoder[A]): CellDecoder[A] =
    da.tag[codecs.type].mapError(e ⇒ TypeError(e))
}
