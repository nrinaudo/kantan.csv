package kantan.csv.scalaz

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import kantan.csv.laws.{IllegalCell, IllegalRow}
import codecs._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import _root_.scalaz.scalacheck.ScalazArbitrary._
import scalaz.Maybe

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def arbMaybeCell[A](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalCell[Maybe[A]]] =
    illegal(arb.arbitrary.map(_.value))

  implicit val arbIllegalRow: Arbitrary[IllegalRow[Maybe[(Int, Int)]]] =
    illegal(Gen.alphaChar.map(s ⇒ Seq(s.toString)))

  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].cellCodec[String, Float])
  checkAll("Maybe[(Int, Int)]", RowCodecTests[Maybe[(Int, Int)]].rowCodec[String, Float])
}