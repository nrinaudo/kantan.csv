package tabulate.laws

import org.scalacheck.{Arbitrary, Gen}

sealed trait Cell {
  def value: String
  def encoded: String
  def map(f: String => String): Cell = Cell(f(value))
}

object Cell {
  case class Escaped private[Cell](override val value: String) extends Cell {
    override def encoded = "\"" + value.replaceAll("\"", "\"\"") + "\""
  }
  case class NonEscaped private[Cell](override val value: String) extends Cell {
    override def encoded =  value
  }
  case object Empty extends Cell {
    override val value   = ""
    override def encoded = ""
  }

  def apply(value: String): Cell =
    if(value == "")                                                            Empty
    else if(value.exists(c => c == '"' || c == ',' || c == '\n' || c == '\r')) Escaped(value)
    else                                                                       NonEscaped(value)


  // - CSV character generators ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val nonEscapedChar: Gen[Char] = Gen.oneOf((0x20 to 0x21) ++ (0x23 to 0x2B) ++ (0x2D to 0x7E)).map(_.toChar)
  val escapedChar: Gen[Char] = Gen.choose(0x20.toChar, 0x7e.toChar)



  // - CSV cell generators ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val escaped: Gen[Escaped] = Gen.nonEmptyListOf(escapedChar).map(v => Escaped(v.mkString))
  val nonEscaped: Gen[NonEscaped] = Gen.nonEmptyListOf(nonEscapedChar).map(v => NonEscaped(v.mkString))
  val cell: Gen[Cell] = Gen.oneOf(escaped, nonEscaped, Gen.const(Empty))
  val nonEmptyCell: Gen[Cell] = Gen.oneOf(escaped, nonEscaped)

  implicit val arbEscaped: Arbitrary[Escaped] = Arbitrary(escaped)
  implicit val arbNonEscaped: Arbitrary[NonEscaped] = Arbitrary(nonEscaped)
  implicit val arbCell: Arbitrary[Cell] = Arbitrary(cell)


  // - CSV row generators ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowOf[C <: Cell](gen: Gen[C]): Gen[List[C]] = Gen.nonEmptyListOf(gen)
  val row: Gen[List[Cell]] = for {
    // Makes sure we don't end up with the non-empty list of the empty cell, which is the empty list.
    head <- Gen.oneOf(escaped, nonEscaped)
    tail <- Gen.listOf(cell)
  } yield head :: tail

  implicit val arbEscapedRow: Arbitrary[List[Escaped]] = Arbitrary(rowOf(escaped))
  implicit val arbNonEscapedRow: Arbitrary[List[NonEscaped]] = Arbitrary(rowOf(nonEscaped))
  implicit val arbRow: Arbitrary[List[Cell]] = Arbitrary(row)


  // - CSV generators --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def csvOf[C <: Cell](gen: Gen[C]): Gen[List[List[C]]] = Gen.nonEmptyListOf(rowOf(gen))

  implicit val arbEscapedCsv: Arbitrary[List[List[Escaped]]] = Arbitrary(csvOf(escaped))
  implicit val arbNonEscapedCsv: Arbitrary[List[List[NonEscaped]]] = Arbitrary(csvOf(nonEscaped))
  implicit val arbCsv: Arbitrary[List[List[Cell]]] = Arbitrary(Gen.nonEmptyListOf(row))
}