package tabulate.interop.cats

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}
import codecs._
import _root_.cats.data.Xor
import _root_.cats.laws.discipline.arbitrary._

class XorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegal: Arbitrary[IllegalValue[Xor[Int, Boolean]]] =
    Arbitrary(Gen.alphaChar.map(i => IllegalValue(i.toString)))

  checkAll("Xor[Int, Boolean]", CellCodecTests[Xor[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Xor[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Xor[(Int, Int, Int), (Boolean, Float)]].reversibleRowCodec[Byte, String])
}