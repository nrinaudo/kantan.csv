package kantan.csv

package object engine {
  /** Converts a java iterator of arrays of strings into something that can be used with
    * [[kantan.csv.CsvReader.fromUnsafe]].
    */
  implicit def javaIterator(it: java.util.Iterator[Array[String]]): Iterator[Seq[String]] = new Iterator[Seq[String]] {
    @inline override def hasNext = it.hasNext
    @inline override def next() = it.next()
  }
}
