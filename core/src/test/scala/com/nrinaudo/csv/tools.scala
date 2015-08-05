package com.nrinaudo.csv

import java.io.{PrintWriter, StringWriter}

import scala.io.Source

private[csv] object tools {
  // Helper functions to turn a CSV stream into a List[List[String]].
  def read(source: Source): List[List[String]] = rowsR[List[String]](source, ',').toList
  def readResource(str: String): List[List[String]] = read(Source.fromInputStream(getClass.getResourceAsStream(str)))
  def read(str: String): List[List[String]] = read(Source.fromString(str))

  // Helper function for writing CSV data to a string.
  def write(ss: List[List[String]]): String = {
    val sw = new StringWriter()
    val out = rowsW[List[String]](new PrintWriter(sw), ',')
    ss.foreach { s => out.write(s) }
    sw.toString
  }
}
