package tabulate.interop.scalaz

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

import codecs._
import _root_.scalaz.\/
import _root_.scalaz.scalacheck.ScalazArbitrary._

class DisjunctionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegal: Arbitrary[IllegalValue[Int \/ Boolean]] =
    Arbitrary(Gen.alphaChar.map(i => IllegalValue(i.toString)))

  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Boolean].cellCodec[Byte, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, Float)", RowCodecTests[(Int, Int, Int) \/ (Boolean, Float)].reversibleRowCodec[Byte, String])
}