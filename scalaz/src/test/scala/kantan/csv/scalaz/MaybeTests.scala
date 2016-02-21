package kantan.csv.scalaz

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import codecs._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import scalaz.{\/, Maybe}

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: should this be moved into a kantan.codecs-laws-scalaz project? I'm bound to need to re-use it.
  implicit def arbLegalMaybe[E, D](implicit al: Arbitrary[LegalValue[E, Option[D]]]): Arbitrary[LegalValue[E, Maybe[D]]] =
    Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))

  implicit def arbIllegalMaybe[E, D](implicit al: Arbitrary[IllegalValue[E, Option[D]]]): Arbitrary[IllegalValue[E, Maybe[D]]] =
      Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))


  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].codec[String, Float])
  checkAll("Maybe[(Int, Int)]", RowCodecTests[Maybe[(Int, Int)]].codec[String, Float])
}