package kantan.csv.joda.time

import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.CellCodecTests
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = ISODateTimeFormat.dateTime

  checkAll("CellCodec[DateTime]", CellCodecTests[DateTime].codec[String, Float])
}
