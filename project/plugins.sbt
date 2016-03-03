resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases,
  "jgit-repo" at "http://download.eclipse.org/jgit/maven"
)

addSbtPlugin("org.xerial.sbt"     %  "sbt-sonatype"    % "0.5.0")
addSbtPlugin("com.jsuereth"       %  "sbt-pgp"         % "1.0.0")
addSbtPlugin("org.scoverage"      %% "sbt-scoverage"   % "1.3.3")
addSbtPlugin("org.tpolecat"       %  "tut-plugin"      % "0.4.2")
addSbtPlugin("com.typesafe.sbt"   %  "sbt-site"        % "0.8.1")
addSbtPlugin("com.eed3si9n"       %  "sbt-unidoc"      % "0.3.3")
addSbtPlugin("com.typesafe.sbt"   %  "sbt-ghpages"     % "0.5.4")
addSbtPlugin("pl.project13.scala" %  "sbt-jmh"         % "0.2.6")
addSbtPlugin("io.spray"           %  "sbt-boilerplate" % "0.6.0")
