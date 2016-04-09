package kantan.csv.joda.time

import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.CellCodecTests
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumDateTime()

  checkAll("CellCodec[LocalDateTime]", CellCodecTests[LocalDateTime].codec[String, Float])
}
