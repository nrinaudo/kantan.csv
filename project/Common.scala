import sbt._
import Keys._

object Common extends Build {
  val pom = {
    <url>https://github.com/nrinaudo/scalaz-csv</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/nrinaudo/scalaz-csv.git</connection>
        <developerConnection>scm:git:git@github.com:nrinaudo/scalaz-csv.git</developerConnection>
        <url>github.com/nrinaudo/scalaz-csv.git</url>
      </scm>
      <developers>
        <developer>
          <id>nrinaudo</id>
          <name>Nicolas Rinaudo</name>
          <url>http://nrinaudo.github.io</url>
        </developer>
      </developers>
  }


  override val settings = super.settings ++
  Seq(organization      :=  "com.nrinaudo",
      version            :=  "0.1.3",
      scalaVersion       :=  "2.11.7",
      scalacOptions      ++= Seq("-deprecation",
                                 "-encoding", "UTF-8",
                                 "-feature",
                                 "-language:existentials",
                                 "-language:higherKinds",
                                 "-language:implicitConversions",
                                 "-unchecked",
                                 "-Xfatal-warnings",
                                 "-Xlint",
                                 "-Yno-adapted-args",
                                 "-Ywarn-dead-code",
                                 "-Ywarn-numeric-widen",
                                 "-Ywarn-value-discard",
                                 "-Xfuture"),
      incOptions         := incOptions.value.withNameHashing(true),
      pomExtra           := pom
    )}
