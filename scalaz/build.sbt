name                := "tabulate-scalaz"

resolvers           += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz"     %% "scalaz-core"               % "7.1.4"

libraryDependencies += "org.scalaz"     %% "scalaz-scalacheck-binding" % "7.1.4"    % "test"

libraryDependencies += "org.scalatest"  %% "scalatest"                 % "3.0.0-M7" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck"                % "1.12.5"   % "test"
