package kantan.csv.cats

import cats.std.all._
import cats.laws.discipline.{ContravariantTests, FunctorTests}
import arbitrary._
import equality._
import kantan.csv._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class InstancesTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellDecoder", FunctorTests[CellDecoder].functor[Int, Int, Int])
  checkAll("RowDecoder", FunctorTests[RowDecoder].functor[Int, Int, Int])
  checkAll("CellEncoder", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])
  //checkAll("RowEncoder", ContravariantTests[RowEncoder].contravariant[Int, Int, Int])
}
