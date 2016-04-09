package kantan.csv

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import kantan.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH)

  checkAll("CellCodec[Date]", CellCodecTests[Date].codec[String, Float])
}
