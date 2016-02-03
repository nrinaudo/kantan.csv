package tabulate.interop.cats

import cats.Eq
import cats.syntax.eq._

object eqs {
  implicit def eqTuple3[A1: Eq, A2: Eq, A3: Eq] = new Eq[(A1, A2, A3)] {
    override def eqv(x: (A1, A2, A3), y: (A1, A2, A3)): Boolean =
    x._1 === y._1 && x._2 === y._2 && x._3 === y._3
  }
}
