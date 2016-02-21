package kantan.csv

import java.util.UUID

import kantan.csv.laws.discipline.CellCodecTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbUUID = Arbitrary(Gen.uuid)

  checkAll("UUID", CellCodecTests[UUID].codec[String, Float])
}
