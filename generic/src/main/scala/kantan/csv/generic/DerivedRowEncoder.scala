package kantan.csv.generic

import kantan.csv
import kantan.csv.CellEncoder
import shapeless._

trait DerivedRowEncoder[A] extends csv.RowEncoder[A]

@export.exports
object DerivedRowEncoder {
  def apply[A](f: A ⇒ Seq[String]): DerivedRowEncoder[A] = new DerivedRowEncoder[A] {
    override def encode(a: A) = f(a)
  }



  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlist[H, T <: HList](implicit eh: CellEncoder[H], et: DerivedRowEncoder[T]): DerivedRowEncoder[H :: T] =
    DerivedRowEncoder((a: H :: T) ⇒ a match {
      case h :: t ⇒ eh.encode(h) +: et.encode(t)
    })

  implicit val hnil: DerivedRowEncoder[HNil] = DerivedRowEncoder(_ ⇒ Seq.empty)

  // Case objects or case classes of arity 0 are a special case: they only encode to empty sequences.
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedRowEncoder[A] =
    DerivedRowEncoder(_ ⇒ Seq.empty)

  // Case classes of arity 1 are a special case: if the unique field has a row encoder, than we can consider that the
  // whole case class encodes exactly as its field does.
  implicit def caseClass1[A, H, R <: HList](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), eh: DerivedRowEncoder[H]): DerivedRowEncoder[A] =
    DerivedRowEncoder(a ⇒ eh.encode(ev(gen.to(a)).head))

  // Case class of arity >= 2
  implicit def caseClassN[A, H1, H2, R <: HList](implicit gen: Generic.Aux[A, R], ev: R <:< (H1 :: H2 :: HList), er: DerivedRowEncoder[R]): DerivedRowEncoder[A] =
    DerivedRowEncoder(a ⇒ er.encode(gen.to(a)))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H, T <: Coproduct](implicit eh: csv.RowEncoder[H], et: DerivedRowEncoder[T]): DerivedRowEncoder[H :+: T] =
    DerivedRowEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnil: DerivedRowEncoder[CNil] =
    DerivedRowEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], er: DerivedRowEncoder[R]): DerivedRowEncoder[A] =
    DerivedRowEncoder(a ⇒ er.encode(gen.to(a)))
}