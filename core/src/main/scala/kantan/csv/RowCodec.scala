package kantan.csv

trait RowCodec[A] extends RowDecoder[A] with RowEncoder[A]

@export.exports(Subclass)
object RowCodec extends GeneratedRowCodecs {
  implicit def combine[C](implicit r: RowDecoder[C], w: RowEncoder[C]): RowCodec[C] = RowCodec(r.decode _ , w.encode _)

  def apply[C](decoder: Seq[String] ⇒ DecodeResult[C], encoder: C ⇒ Seq[String]): RowCodec[C] = new RowCodec[C] {
    override def encode(a: C) = encoder(a)
    override def decode(row: Seq[String]) = decoder(row)
  }
}
