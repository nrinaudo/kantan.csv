package kantan.csv

import java.net.URI
import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class URICodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[URI]", CellCodecTests[URI].codec[String, Float])
  checkAll("RowCodec[URI]", RowCodecTests[URI].codec[String, Float])
}
