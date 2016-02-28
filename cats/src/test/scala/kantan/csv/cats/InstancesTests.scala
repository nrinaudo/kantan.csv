package kantan.csv.cats

import cats._
import cats.std.all._
import cats.laws.discipline.{ContravariantTests, FunctorTests}
import kantan.codecs.cats.laws._
import arbitrary._
import kantan.csv._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class InstancesTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: needs Eq[Seq[String]]

  implicit def cellDecoderEq[D: Eq] = decoderEq[String, D, DecodeError, CellDecoder]
  implicit def rowDecoderEq[D: Eq] = decoderEq[Seq[String], D, DecodeError, RowDecoder]
  implicit def cellEncoderEq[D: Arbitrary] = encoderEq[String, D, CellEncoder]
  //implicit def rowEncoderEq[D: Arbitrary] = encoderEq[Seq[String], D, RowEncoder]

  checkAll("CellDecoder", FunctorTests[CellDecoder].functor[Int, Int, Int])
  checkAll("RowDecoder", FunctorTests[RowDecoder].functor[Int, Int, Int])
  checkAll("CellEncoder", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])
  //checkAll("RowEncoder", ContravariantTests[RowEncoder].contravariant[Int, Int, Int])
}
