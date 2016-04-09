package kantan.csv.joda.time

import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.CellCodecTests
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumTime()

  checkAll("CellCodec[LocalTime]", CellCodecTests[LocalTime].codec[String, Float])
}
