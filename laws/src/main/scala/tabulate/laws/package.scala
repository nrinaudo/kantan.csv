package tabulate

package object laws {
  type ExpectedCell[A] = ExpectedValue[A, String]
  type ExpectedRow[A] = ExpectedValue[A, Seq[String]]
}
