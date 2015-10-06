import sbt._
import sbt.Keys._

object Common extends Build {
  val pom = {
    <url>https://nrinaudo.github.io/tabulate/</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/nrinaudo/tabulate.git</connection>
        <developerConnection>scm:git:git@github.com:nrinaudo/tabulate.git</developerConnection>
        <url>github.com/nrinaudo/tabulate.git</url>
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
                          Seq(organization     :=  "com.nrinaudo",
                            version            :=  "0.1.4-SNAPSHOT",
                            scalaVersion       :=  "2.11.7",
                            crossScalaVersions := Seq("2.10.5", "2.11.7"),
                            addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
                            libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.4.0",
                            scalacOptions     ++= Seq("-deprecation",
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
