package kantan.csv.scalaz

import codecs._
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import scalaz.{Maybe, \/}

class DisjunctionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: should this be moved into a kantan.codecs-laws-scalaz project? I'm bound to need to re-use it.
  implicit def arbLegalDisjunction[E, DL, DR](implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL \/ DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))

  implicit def arbIllegalDisjunction[E, DL, DR](implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL \/ DR]] =
      Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))

  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Boolean].codec[Byte, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, Float)", RowCodecTests[(Int, Int, Int) \/ (Boolean, Float)].codec[Byte, String])
}