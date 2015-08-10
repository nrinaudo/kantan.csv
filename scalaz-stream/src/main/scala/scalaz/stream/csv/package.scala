package scalaz.stream

import java.io._

import com.nrinaudo.csv._

import scala.collection.mutable.ArrayBuffer
import scala.io.{Codec, Source}
import scalaz.concurrent.Task

package object csv {
  // - Unsafe sources --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def unsafeRowsR(src: => Source, sep: Char): Process[Task, ArrayBuffer[String]] =
    io.resource(Task.delay(src))(src => Task.delay(src.close())) { src =>
      lazy val lines = com.nrinaudo.csv.unsafeRowsR(src, sep)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }

  def unsafeRowsR(file: String, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
    unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(file: File, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
    unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(in: InputStream, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
    unsafeRowsR(Source.fromInputStream(in), sep)



  // - Typeclass-based sources -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsR[A](src: => Source, sep: Char)(implicit r: RowReader[A]): Process[Task, A] = {
    val data = unsafeRowsR(src, sep)
    if(r.hasHeader) data.drop(1).map(r.read)
    else            data.map(r.read)
  }

  def rowsR[A: RowReader](file: String, sep: Char)(implicit c: Codec): Process[Task, A] =
    rowsR(Source.fromFile(file), sep)

  def rowsR[A: RowReader](file: File, sep: Char)(implicit c: Codec): Process[Task, A] =
    rowsR(Source.fromFile(file), sep)

  def rowsR[A: RowReader](in: InputStream, sep: Char)(implicit c: Codec): Process[Task, A] =
    rowsR(Source.fromInputStream(in), sep)



  // - Typeclass-based sinks -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsW[A: RowWriter](out: => PrintWriter, sep: Char): Sink[Task, A] =
    io.resource(Task.delay(com.nrinaudo.csv.rowsW(out, sep)))(out => Task.delay(out.close()))(
      out => Task.now((a: A) => Task.delay { out.write(a); () })
    )

  def rowsW[A: RowWriter](file: File, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](file: String, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](out: => OutputStream, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new PrintWriter(new OutputStreamWriter(out, c.charSet)), sep)
}
