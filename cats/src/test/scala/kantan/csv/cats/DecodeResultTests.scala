package kantan.csv.cats

import cats.laws.discipline.MonadTests
import cats.std.int._
import kantan.csv.DecodeResult
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.cats.eqs._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("DecodeResult[Int]", MonadTests[DecodeResult].monad[Int, Int, Int])
}