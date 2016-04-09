package kantan.csv.joda.time

import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.CellCodecTests
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumDate()

  checkAll("CellCodec[LocalDate]", CellCodecTests[LocalDate].codec[String, Float])
}
