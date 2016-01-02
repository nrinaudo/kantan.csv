package tabulate.laws

/** Represents a value that cannot be decoded as an `A`. */
case class IllegalValue[A, B](value: B)