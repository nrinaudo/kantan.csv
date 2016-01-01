package tabulate.interop.scalaz

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{RowCodecTests, CellCodecTests}

import codecs._
import _root_.scalaz.scalacheck.ScalazArbitrary._
import scalaz.Maybe

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def arbMaybe[A](implicit arb: Arbitrary[IllegalValue[A]]): Arbitrary[IllegalValue[Maybe[A]]] =
      Arbitrary(arb.arbitrary.map(a => IllegalValue(a.value)))

  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].cellCodec[String, Float])
  checkAll("Maybe[(Int, Int)]", RowCodecTests[Maybe[(Int, Int)]].rowCodec[String, Float])
}