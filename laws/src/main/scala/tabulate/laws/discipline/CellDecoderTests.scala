package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.CellDecoder
import tabulate.laws._

trait CellDecoderTests[A] extends SafeCellDecoderTests[A] with RowDecoderTests[A] {
  def laws: CellDecoderLaws[A]
  implicit def arbIllegalCell: Arbitrary[IllegalCell[A]]

  def cellDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new RuleSet {
    override def name = "cellDecoder"
    override def bases = Seq.empty
    override def props = Seq(
      "safe cell decode fail"     -> forAll(laws.safeCellDecodeFail _),
      "unsafe cell decode fail"   -> forAll(laws.unsafeCellDecodeFail _))
    override def parents = Seq(safeCellDecoder[B, C], rowDecoder[B, C])
  }
}

object CellDecoderTests {
  def apply[A](implicit ac: Arbitrary[ExpectedCell[A]], ar: Arbitrary[ExpectedRow[A]], c: CellDecoder[A], ic: Arbitrary[IllegalCell[A]], ia: Arbitrary[IllegalRow[A]]): CellDecoderTests[A] = new CellDecoderTests[A] {
    override def laws = CellDecoderLaws[A]
    override implicit def arbExpectedCell = ac
    override implicit def arbExpectedRow = ar
    override implicit def arbIllegalCell = ic
    override implicit def arbIllegalRow = ia
  }
}