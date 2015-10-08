name                := "tabulate-scalaz-stream"

resolvers           += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

libraryDependencies += "com.github.mpilquist" %% "simulacrum"    % "0.4.0"

libraryDependencies += "org.scalaz.stream"    %% "scalaz-stream" % "0.7.3"

libraryDependencies += "org.scalatest"        %% "scalatest"     % "3.0.0-M7" % "test"

libraryDependencies += "org.scalacheck"       %% "scalacheck"    % "1.12.5"   % "test"
