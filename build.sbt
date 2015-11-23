import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._

val catsVersion          = "0.3.0"
val exportHookVersion    = "1.1.0"
val simulacrumVersion    = "0.5.0"
val macroParadiseVersion = "2.1.0-M5"
val scalaCheckVersion    = "1.12.5"
val disciplineVersion    = "0.4"
val shapelessVersion     = "2.2.5"
val scalatestVersion     = "3.0.0-M7"
val scalazVersion        = "7.1.5"
val scalazStreamVersion  = "0.8"


lazy val buildSettings = Seq(
  organization       := "com.nrinaudo",
  scalaVersion       := "2.11.7",
  crossScalaVersions := Seq("2.10.6", "2.11.7")
)

lazy val compilerOptions = Seq("-deprecation",
  "-target:jvm-1.7",
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
  "-Xfuture")

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions,
  //resolvers += Resolver.sonatypeRepo("snapshots"),
  libraryDependencies ++= Seq(
    "org.typelevel"        %% "export-hook"   % exportHookVersion,
    "org.scala-lang"        % "scala-reflect" % scalaVersion.value  % "provided",
    "com.github.mpilquist" %% "simulacrum"    % simulacrumVersion % "provided",
    compilerPlugin("org.scalamacros" % "paradise" % macroParadiseVersion cross CrossVersion.full)
  ),
  incOptions         := incOptions.value.withNameHashing(true)
)

lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://nrinaudo.github.io/tabulate/")),
  licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  apiURL := Some(url("https://nrinaudo.github.io/tabulate/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nrinaudo/tabulate"),
      "scm:git:git@github.com:nrinaudo/tabulate.git"
    )
  ),
  pomExtra := <developers>
    <developer>
      <id>nrinaudo</id>
      <name>Nicolas Rinaudo</name>
      <url>http://nrinaudo.github.io</url>
    </developer>
  </developers>
)


lazy val allSettings = buildSettings ++ baseSettings ++ publishSettings

lazy val root = Project(id = "tabulate", base = file("."))
  .settings(moduleName := "root")
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    initialCommands in console :=
    """
      |import tabulate._
      |import tabulate.ops._
      |import tabulate.generic.codecs._
    """.stripMargin
  )
  .aggregate(core, cats, scalaz, scalazStream, laws, tests, docs, generic)
  .dependsOn(core, generic, scalaz, cats)

lazy val core = project
  .settings(
    moduleName := "tabulate",
    name       := "core"
  )
  .settings(allSettings: _*)

lazy val laws = project
  .settings(
    moduleName := "tabulate-laws",
    name       := "laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
    "org.typelevel"  %% "discipline" % disciplineVersion
  ))
  .settings(allSettings: _*)
  .dependsOn(core)

lazy val generic = project
  .settings(
    moduleName := "tabulate-generic",
    name       := "generic"
  )
  .settings(libraryDependencies ++= Seq(
    "com.chuusai"   %% "shapeless" % shapelessVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws % "test")

lazy val scalaz = project
  .settings(
    moduleName := "tabulate-scalaz",
    name       := "scalaz"
  )
  .settings(allSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz"    %% "scalaz-core"               % scalazVersion,
    "org.scalaz"    %% "scalaz-scalacheck-binding" % scalazVersion    % "test",
    "org.scalatest" %% "scalatest"                 % scalatestVersion % "test"
  ))
  .dependsOn(core, laws % "test")

lazy val scalazStream = Project(id = "scalaz-stream", base = file("scalaz-stream"))
  .settings(
    moduleName := "tabulate-scalaz-stream",
    name       := "scalaz-stream"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalaz.stream" %% "scalaz-stream" % scalazStreamVersion,
    "org.scalatest"     %% "scalatest"     % scalatestVersion % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(scalaz, laws % "test")

lazy val cats = project
  .settings(
    moduleName := "tabulate-cats",
    name       := "cats"
  )
  .settings(libraryDependencies ++= Seq(
    "org.spire-math" %% "cats"      % catsVersion,
    "org.spire-math" %% "cats-laws" % catsVersion      % "test",
    "org.scalatest"  %% "scalatest" % scalatestVersion % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws % "test")

lazy val tests = project
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(coverageExcludedPackages := "tabulate\\.laws\\..*")
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")
  .dependsOn(core, laws % "test")

lazy val benchmark = project
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .enablePlugins(JmhPlugin)
  .dependsOn(core)

lazy val docs = project
  .settings(allSettings: _*)
  .settings(site.settings: _*)
  .settings(ghpages.settings: _*)
  .settings(unidocSettings: _*)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmark)
  )
  .settings(tutSettings: _*)
  .settings(
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api"),
    site.addMappingsToSiteDir(tut, "_tut"),
    git.remoteRepo := "git@github.com:nrinaudo/tabulate.git",
    ghpagesNoJekyll := false,
    includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" |
                                 "*.eot" | "*.svg" | "*.ttf" | "*.woff" | "*.woff2" | "*.otf"
  )
  .settings(noPublishSettings:_*)
  .dependsOn(core, scalazStream, laws, cats, scalaz, generic)

addCommandAlias("runBench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv")
