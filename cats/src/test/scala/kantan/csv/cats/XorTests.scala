package kantan.csv.cats

import codecs._
import _root_.cats.data.Xor
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class XorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: should this be moved into a kantan.codecs-laws-cats project? I'm bound to need to re-use it.
  implicit def arbLegalXor[E, DL, DR](implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))

  implicit def arbIllegalXor[E, DL, DR](implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))

  checkAll("Xor[Int, Boolean]", CellCodecTests[Xor[Int, Boolean]].codec[Byte, Float])
  checkAll("Xor[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Xor[(Int, Int, Int), (Boolean, Float)]].codec[Byte, String])
}