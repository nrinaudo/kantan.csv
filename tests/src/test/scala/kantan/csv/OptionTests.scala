package kantan.csv

import kantan.codecs.laws.discipline.GenCodecValue
import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OptionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val test: GenCodecValue[Seq[String], (Int, Int, Int)] =
    GenCodecValue.nonFatal[Seq[String], (Int, Int, Int)](is ⇒ Seq(is._1.toString, is._2.toString, is._3.toString)) { ss ⇒
      if(ss.length > 3) sys.error("too many")
      else (ss(0).toInt, ss(1).toInt, ss(2).toInt)
    }

  checkAll("CellCodec[Option[Int]]", CellCodecTests[Option[Int]].codec[String, Float])
  checkAll("RowCodec[Option[(Int, Int, Int)]]", RowCodecTests[Option[(Int, Int, Int)]].codec[Byte, String])
}
