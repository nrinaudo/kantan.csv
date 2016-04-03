package kantan.csv

import shapeless._

package object generic {
  // - CellDecoder ADT derivation --------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellDecoder[H, T <: Coproduct]
  (implicit dh: CellDecoder[H], dt: CellDecoder[T]): CellDecoder[H :+: T] =
    CellDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply)))

  implicit val cnilCellDecoder: CellDecoder[CNil] = CellDecoder(_ ⇒ DecodeResult.outOfBounds(0))

  implicit def adtCellDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: CellDecoder[R]): CellDecoder[A] =
    CellDecoder(row ⇒ dr.decode(row).map(gen.from))



  // - CellEncoder ADT derivation --------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellEncoder[H, T <: Coproduct]
  (implicit eh: CellEncoder[H], et: CellEncoder[T]): CellEncoder[H :+: T] =
    CellEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnilCellEncoder: CellEncoder[CNil] =
    CellEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))

  implicit def adtCellEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], er: CellEncoder[R]): CellEncoder[A] =
    CellEncoder(a ⇒ er.encode(gen.to(a)))



  // - RowDecoder case class derivation --------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistRowDecoder[H, T <: HList](implicit dh: CellDecoder[H], dt: RowDecoder[T]): RowDecoder[H :: T] =
    RowDecoder(row ⇒
      row.headOption.map(s ⇒
        for {
          h ← dh.decode(s)
          t ← dt.decode(row.tail)
        } yield h :: t
      ).getOrElse(DecodeResult.outOfBounds(0)))

  implicit val hnilRowDecoder: RowDecoder[HNil] = RowDecoder(_ ⇒ DecodeResult.success(HNil))

  // Case objects or case classes of arity 0 are a special case: they only decode empty strings.
  implicit def caseObjectRowDecoder[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): RowDecoder[A] =
    RowDecoder(s ⇒ if(s.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.outOfBounds(0))

  // Case classes of arity 1 are a special case: if the unique field has a row decoder, than we can consider that the
  // whole case class decodes exactly as its field does.
  implicit def caseClass1RowDecoder[A, H, R <: HList]
  (implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: RowDecoder[H]): RowDecoder[A] =
    RowDecoder(s ⇒ dh.decode(s).map(h ⇒ gen.from(ev(h :: HNil))))

  // Case class of arity 2+
  implicit def caseClassNRowDecoder[A, H1, H2, R <: HList]
  (implicit gen: Generic.Aux[A, R], ev: R <:< (H1 :: H2 :: HList), dr: RowDecoder[R]): RowDecoder[A] =
    RowDecoder(s ⇒ dr.decode(s).map(gen.from))



  // - RowEncoder case class derivation --------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistRowEncoder[H, T <: HList](implicit eh: CellEncoder[H], et: RowEncoder[T]): RowEncoder[H :: T] =
    RowEncoder((a: H :: T) ⇒ a match {
      case h :: t ⇒ eh.encode(h) +: et.encode(t)
    })

  implicit val hnilRowEncoder: RowEncoder[HNil] = RowEncoder(_ ⇒ Seq.empty)

  // Case objects or case classes of arity 0 are a special case: they only encode to empty sequences.
  implicit def caseObjectRowEncoder[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): RowEncoder[A] =
    RowEncoder(_ ⇒ Seq.empty)

  // Case classes of arity 1 are a special case: if the unique field has a row encoder, than we can consider that the
  // whole case class encodes exactly as its field does.
  implicit def caseClass1RowEncoder[A, H, R <: HList]
  (implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), eh: RowEncoder[H]): RowEncoder[A] =
    RowEncoder(a ⇒ eh.encode(ev(gen.to(a)).head))

  // Case class of arity >= 2
  implicit def caseClassNRowEncoder[A, H1, H2, R <: HList]
  (implicit gen: Generic.Aux[A, R], ev: R <:< (H1 :: H2 :: HList), er: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a ⇒ er.encode(gen.to(a)))



  // - CellDecoder case class derivation -------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseObjectCellDecoder[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): CellDecoder[A] =
    CellDecoder(s ⇒ if(s.trim.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.outOfBounds(0))

  implicit def caseClassCellDecoder[A, R, H]
  (implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: CellDecoder[H]): CellDecoder[A] =
    CellDecoder(s ⇒ dh.decode(s).map(h ⇒ gen.from(ev(h :: HNil))))



  // - CellEncoder case class derivation -------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseObjectCellEncoder[A, R <: HNil](implicit gen: Generic.Aux[A, R]): CellEncoder[A] =
    CellEncoder((_: A) ⇒ "")

  // Thanks Travis Brown for that one:
  // http://stackoverflow.com/questions/33563111/deriving-type-class-instances-for-case-classes-with-exactly-one-field
  implicit def caseClassCellEncoder[A, R, H]
  (implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), eh: CellEncoder[H]): CellEncoder[A] =
    CellEncoder((a: A) ⇒ ev(gen.to(a)) match {
      case h :: t ⇒ eh.encode(h)
    })



  // - RowDecoder ADT derivation ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowDecoder[H, T <: Coproduct]
  (implicit dh: RowDecoder[H], dt: RowDecoder[T]): RowDecoder[H :+: T] =
    RowDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply)))

  implicit val cnilRowDecoder: RowDecoder[CNil] = RowDecoder(_ ⇒ DecodeResult.outOfBounds(0))

  implicit def adtRowDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: RowDecoder[R]): RowDecoder[A] =
    RowDecoder(row ⇒ dr.decode(row).map(gen.from))



  // - RowEncoder ADT derivation ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowEncoder[H, T <: Coproduct]
  (implicit eh: RowEncoder[H], et: RowEncoder[T]): RowEncoder[H :+: T] =
    RowEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnilRowEncoder: RowEncoder[CNil] =
    RowEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))

  implicit def adtRowEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], er: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a ⇒ er.encode(gen.to(a)))
}
