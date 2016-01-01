package tabulate.interop.scalaz

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

import codecs._
import _root_.scalaz.\/
import _root_.scalaz.scalacheck.ScalazArbitrary._

class DisjunctionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegal: Arbitrary[IllegalValue[Int \/ Char]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(i => IllegalValue(i.toString)))

  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Char].cellCodec[Byte, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, Float)", RowCodecTests[(Int, Int, Int) \/ (Boolean, Float)].reversibleRowCodec[Byte, String])
}