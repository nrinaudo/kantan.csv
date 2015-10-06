resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.url("tpolecat-sbt-plugin-releases", url("http://dl.bintray.com/content/tpolecat/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.3.3")

addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")
