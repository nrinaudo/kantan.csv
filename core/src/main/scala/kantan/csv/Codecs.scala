package kantan.csv

import java.util.UUID

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
  // - Primitive type codecs -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val stringCellCodec: CellCodec[String] = CellCodec(s ⇒ DecodeResult.success(s))(identity)
  implicit val charCellCodec: CellCodec[Char] = CellCodec{ s ⇒ DecodeResult {
    if(s.length == 1) s.charAt(0)
    else throw new IllegalArgumentException(s"Not a valid char: '$s'")
  }}(_.toString)
  implicit val intCellCodec: CellCodec[Int] = CellCodec(s ⇒ DecodeResult(s.toInt))(_.toString)
  implicit val floatCellCodec: CellCodec[Float] = CellCodec(s ⇒ DecodeResult(s.toFloat))(_.toString)
  implicit val doubleCellCodec: CellCodec[Double] = CellCodec(s ⇒ DecodeResult(s.toDouble))(_.toString)
  implicit val longCellCodec: CellCodec[Long] = CellCodec(s ⇒ DecodeResult(s.toLong))(_.toString)
  implicit val shortCellCodec: CellCodec[Short] = CellCodec(s ⇒ DecodeResult(s.toShort))(_.toString)
  implicit val byteCellCodec: CellCodec[Byte] = CellCodec(s ⇒ DecodeResult(s.toByte))(_.toString)
  implicit val boolCellCodec: CellCodec[Boolean] = CellCodec(s ⇒ DecodeResult(s.toBoolean))(_.toString)
  implicit val bigIntCellCodec: CellCodec[BigInt] = CellCodec(s ⇒ DecodeResult(BigInt(s)))(_.toString)
  implicit val bigDecCellCodec: CellCodec[BigDecimal] = CellCodec(s ⇒ DecodeResult(BigDecimal(s)))(_.toString)
  implicit val uuidCellCodec: CellCodec[UUID] = CellCodec(s ⇒ DecodeResult(UUID.fromString(s)))(_.toString)
  implicit def stringSeqRowCodec: RowCodec[Seq[String]] = RowCodec(ss ⇒ DecodeResult(ss))(identity)



  // - Type constructor codecs -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def optCellDecoder[A](implicit da: CellDecoder[A]): CellDecoder[Option[A]] = CellDecoder { s ⇒
    if(s.isEmpty) DecodeResult.success(None)
    else          da.decode(s).map(Option.apply)
  }

  implicit def optCellEncoder[A](implicit ea: CellEncoder[A]): CellEncoder[Option[A]] =
    CellEncoder(oa ⇒ oa.map(ea.encode).getOrElse(""))

  implicit def eitherCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[Either[A, B]] =
    CellDecoder { s ⇒ da.decode(s).map(a ⇒ Left(a): Either[A, B])
      .orElse(db.decode(s).map(b ⇒ Right(b): Either[A, B]))
    }

  implicit def eitherCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[Either[A, B]] =
    CellEncoder(eab ⇒ eab match {
      case Left(a)  ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    })

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
