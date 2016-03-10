package kantan.csv

import kantan.codecs.Decoder

import scala.collection.generic.CanBuildFrom

object RowDecoder extends GeneratedRowDecoders {
  def apply[A](implicit da: RowDecoder[A]): RowDecoder[A] = da
  def apply[A](f: Seq[String] ⇒ DecodeResult[A]): RowDecoder[A] = Decoder(f)
}

trait RowDecoderInstances {
  implicit def fromCellDecoder[A](implicit da: CellDecoder[A]): RowDecoder[A] = RowDecoder(ss ⇒
    ss.headOption.map(h ⇒ if(ss.tail.isEmpty) da.decode(h) else DecodeResult.outOfBounds(1)).getOrElse(DecodeResult.outOfBounds(0))
  )

  implicit def eitherRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Either[A, B]] = RowDecoder { ss ⇒
    da.decode(ss).map(a ⇒ Left(a): Either[A, B]).orElse(db.decode(ss).map(b ⇒ Right(b): Either[A, B]))
  }

  implicit def optionRowDecoder[A](implicit da: RowDecoder[A]): RowDecoder[Option[A]] = RowDecoder { ss ⇒
    if(ss.isEmpty) DecodeResult.success(None)
    else           da.decode(ss).map(a ⇒ Some(a))
  }

  implicit def collectionRowDecoder[A, M[X]](implicit da: CellDecoder[A], cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder(ss ⇒ ss.foldLeft(DecodeResult(cbf.apply())) { (racc, s) ⇒ for {
      acc ← racc
      a   ← da.decode(s)
    } yield acc += a
    }.map(_.result()))
}
