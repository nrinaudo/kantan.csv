package kantan.csv

import java.util.UUID
import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbUUID = Arbitrary(Gen.uuid)

  checkAll("CellCodec[UUID]", CellCodecTests[UUID].codec[String, Float])
  checkAll("RowCodec[UUID]", RowCodecTests[UUID].codec[String, Float])
}
