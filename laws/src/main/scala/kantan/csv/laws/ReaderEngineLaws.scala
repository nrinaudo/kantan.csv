package kantan.csv.laws

import kantan.csv.CsvInput
import kantan.csv.engine.ReaderEngine
import kantan.csv.ops._
import org.scalacheck.Prop._

trait ReaderEngineLaws extends RfcReaderLaws with SpectrumReaderLaws with KnownFormatsReaderLaws {
  private def asReader(csv: List[List[Cell]]): kantan.csv.CsvReader[List[Cell]] =
    CsvInput[String].unsafeReader[List[Cell]](csv.asCsv(','), ',', false)

  def nextOnEmpty(csv: List[List[Cell]]): Boolean = {
    val data = asReader(csv)

    csv.indices.foreach { _ ⇒ data.next() }
    throws(classOf[java.util.NoSuchElementException])(data.next())
  }

  def nextOnEmptyTake(csv: List[List[Cell]], i: Int): Boolean = {
    val data = asReader(csv).take(i)

    csv.take(i).indices.foreach { _ ⇒ data.next() }
    throws(classOf[java.util.NoSuchElementException])(data.next())
  }

  def drop(csv: List[List[Cell]], i: Int): Boolean =
    asReader(csv).drop(i).toList == csv.drop(i)

  def dropWhile(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).dropWhile(f).toList == csv.dropWhile(f)

  def take(csv: List[List[Cell]], i: Int): Boolean =
    asReader(csv).take(i).toList == csv.take(i)

  def forall(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).forall(f) == csv.forall(f)

  def find(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).find(f) == csv.find(f)

  def exists(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).exists(f) == csv.exists(f)

  def filter(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).filter(f).toList == csv.filter(f)

  def withFilter(csv: List[List[Cell]], f: List[Cell] ⇒ Boolean): Boolean =
    asReader(csv).withFilter(f).toList == asReader(csv).filter(f).toList

  def toStream(csv: List[List[Cell]]): Boolean =
    asReader(csv).toStream == csv.toStream

  def toTraversable(csv: List[List[Cell]]): Boolean =
    asReader(csv).toTraversable == csv.toTraversable

  def toIterator(csv: List[List[Cell]]): Boolean =
    asReader(csv).toIterator.sameElements(csv.toIterator)

  def isTraversableAgain(csv: List[List[Cell]]): Boolean =
    !asReader(csv).isTraversableAgain

  def hasDefiniteSize(csv: List[List[Cell]]): Boolean = {
    def loop[A](data: kantan.csv.CsvReader[A]): Boolean =
      if(data.hasNext) !data.hasDefiniteSize && {data.next(); loop(data)}
      else data.hasDefiniteSize

    loop(asReader(csv))
  }

  def isEmpty(csv: List[List[Cell]]): Boolean = {
    def loop[A](data: kantan.csv.CsvReader[A]): Boolean =
      if(data.hasNext) !data.isEmpty && {data.next(); loop(data)}
      else data.isEmpty

    loop(asReader(csv))
  }

  def copyToArray(csv: List[List[Cell]], from: Int, count: Int): Boolean = {
    // Makes sure we only have legal arguments, as copyToArray isn't required to do any validity check
    val f = if(from < 0) 0 else if(from > csv.length - 1) csv.length - 1 else from
    val c = math.max(math.min(count, csv.length - f), 1)

    val a1, a2 = new Array[List[Cell]](c)
    asReader(csv).copyToArray(a1, f, c)
    csv.copyToArray(a2, f, c)
    a1.sameElements(a2)
  }

  def map(csv: List[List[Cell]], f: List[Cell] ⇒ Int): Boolean = asReader(csv).map(f).toList == csv.map(f)

  def flatMap(csv: List[List[Cell]], f: List[Cell] ⇒ List[List[Cell]]): Boolean =
    asReader(csv).flatMap(r ⇒ asReader(f(r))).toList == csv.flatMap(f)
}

object ReaderEngineLaws {
  def apply(e: ReaderEngine): ReaderEngineLaws = new ReaderEngineLaws {
    override implicit val engine = e
  }
}