package kantan.csv

import java.net.URL
import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class URLCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[URL]", CellCodecTests[URL].codec[String, Float])
  checkAll("RowCodec[URL]", RowCodecTests[URL].codec[String, Float])
}
