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



  // - Safe sources ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def safeRowsR(src: => Source, sep: Char): Process[Task, Vector[String]] =
    rowsR[Vector[String]](src, sep)

  def safeRowsR(file: String, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR[Vector[String]](file, sep)

  def safeRowsR(file: File, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR[Vector[String]](file, sep)

  def safeRowsR(in: InputStream, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR[Vector[String]](in, sep)



  // - Typeclass-based sources -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsR[A: RowReader](src: => Source, sep: Char): Process[Task, A] =
    unsafeRowsR(src, sep).map(RowReader[A].read)

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
      out => Task.now((a: A) => Task.delay(out.write(a)))
    )

  def rowsW[A: RowWriter](file: File, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](file: String, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](out: => OutputStream, sep: Char)(implicit c: Codec): Sink[Task, A] =
    rowsW(new PrintStream(out, true, c.charSet.name()), sep)
}
