organization        := "com.nrinaudo"

name                := "scalaz-csv"

version             := "0.1.0"

scalaVersion        := "2.11.6"

scalacOptions       ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint",
                            "-Yno-adapted-args", "-Ywarn-dead-code", "-Ywarn-numeric-widen",
                            "-Ywarn-value-discard", "-Xfuture", "-Ywarn-unused-import" , "-Xfatal-warnings")

incOptions          := incOptions.value.withNameHashing(true)

pomExtra in Global := {
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

resolvers           += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.7.1a"

libraryDependencies += "org.scalatest"     %% "scalatest"     % "2.2.5"   % "test"