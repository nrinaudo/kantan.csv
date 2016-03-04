package kantan.csv

import kantan.codecs.strings.StringCodec
import kantan.csv.DecodeError.TypeError

import scala.collection.generic.CanBuildFrom

trait LowPrioryCellDecoders

trait LowPriorityCodecs {
  implicit def cellDecoder[A](implicit da: CellDecoder[A]): RowDecoder[A] = RowDecoder(ss ⇒
    ss.headOption.map(h ⇒ if(ss.tail.isEmpty) da.decode(h) else DecodeResult.outOfBounds(1)).getOrElse(DecodeResult.outOfBounds(0))
  )

  implicit def traversable[A, M[X] <: TraversableOnce[X]](implicit ea: CellEncoder[A]): RowEncoder[M[A]] =
    RowEncoder { as ⇒ as.foldLeft(Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result() }

  implicit def cellEncoder[A](implicit ea: CellEncoder[A]): RowEncoder[A] = RowEncoder(a ⇒ Seq(ea.encode(a)))
}

object Codecs extends TupleInstances with LowPriorityCodecs {
  // - Cell codecs -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def cellCodec[A](implicit ca: StringCodec[A]): CellCodec[A] = ca.tag[Codecs.type].mapError(e ⇒ TypeError(e))




  // - Row codecs ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def stringSeqRowCodec: RowCodec[Seq[String]] = RowCodec(ss ⇒ DecodeResult(ss))(identity)

  implicit def eitherRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Either[A, B]] = RowDecoder { ss ⇒
    da.decode(ss).map(a ⇒ Left(a): Either[A, B]).orElse(db.decode(ss).map(b ⇒ Right(b): Either[A, B]))
  }

  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Either[A, B]] =
    RowEncoder { ss ⇒ ss match {
      case Left(a) ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}

  implicit def optionRowDecoder[A](implicit da: RowDecoder[A]): RowDecoder[Option[A]] = RowDecoder { ss ⇒
    if(ss.isEmpty) DecodeResult.success(None)
    else           da.decode(ss).map(a ⇒ Some(a))
  }

  implicit def optionRowEncoder[A](implicit ea: RowEncoder[A]): RowEncoder[Option[A]] =
    RowEncoder(_.map(a ⇒ ea.encode(a)).getOrElse(Seq.empty))

  implicit def collectionRowDecoder[A, M[X]](implicit da: CellDecoder[A], cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder(ss ⇒ ss.foldLeft(DecodeResult(cbf.apply())) { (racc, s) ⇒ for {
      acc ← racc
      a   ← da.decode(s)
    } yield acc += a
    }.map(_.result()))
}
