package kantan.csv

import java.io._
import kantan.csv.engine.WriterEngine
import scala.io.Codec

trait CsvOutput[-S] extends Serializable { self ⇒
  def open(s: S): Writer

  def writer[A: RowEncoder](s: S, separator: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): CsvWriter[A] =
    CsvWriter(open(s), separator, header)

  def contramap[T](f: T ⇒ S): CsvOutput[T] = CsvOutput(t ⇒ self.open(f(t)))

  def write[A: RowEncoder](out: S, rows: TraversableOnce[A], sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): Unit =
    writer(out, sep, header).write(rows).close()
}

trait LowPriorityCsvOutputs

object CsvOutput extends LowPriorityCsvOutputs {
  def apply[A](implicit oa: CsvOutput[A]): CsvOutput[A] = oa

  def apply[A](f: A ⇒ Writer): CsvOutput[A] = new CsvOutput[A] {
    override def open(s: A): Writer = f(s)
  }

  implicit def writer: CsvOutput[Writer] = CsvOutput(w ⇒ w)

  implicit def outputStream(implicit codec: Codec): CsvOutput[OutputStream] =
    writer.contramap(o ⇒ new BufferedWriter(new OutputStreamWriter(o, codec.charSet)))
  implicit def file(implicit codec: Codec): CsvOutput[File] = outputStream.contramap(f ⇒ new FileOutputStream(f))
}