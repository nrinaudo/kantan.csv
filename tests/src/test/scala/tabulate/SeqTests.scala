package tabulate

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.RowCodecTests

class SeqTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Seq[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Seq, Int](arbitrary[Int]))

  checkAll("Seq[Int]", RowCodecTests[Seq[Int]].reversibleRowCodec[List[String], List[Float]])
}
