package tabulate

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.RowCodecTests
import tabulate.laws.discipline.arbitrary._

class StreamTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Stream[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Stream, Int](arbitrary[Int]))

  checkAll("Stream[Int]", RowCodecTests[Stream[Int]].rowCodec[List[String], List[Float]])
}
