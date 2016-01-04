import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._

val catsVersion                = "0.3.0"
val exportHookVersion          = "1.1.0"
val simulacrumVersion          = "0.5.0"
val macroParadiseVersion       = "2.1.0"
val scalaCheckVersion          = "1.12.5"
val disciplineVersion          = "0.4"
val shapelessVersion           = "2.2.5"
val scalatestVersion           = "3.0.0-M7"
val scalazVersion              = "7.2.0"
val scalazStreamVersion        = "0.8"
val productCollectionVersion   = "1.4.2"
val opencsvVersion             = "3.6"
val univocityVersion           = "1.5.6"
val jacksonCsvVersion          = "2.6.4"
val commonsCsvVersion          = "1.2"
val scalacheckShapelessVersion = "1.12.1"

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
  libraryDependencies ++= Seq(
    "org.typelevel"        %% "export-hook"   % exportHookVersion,
    "org.scala-lang"        % "scala-reflect" % scalaVersion.value  % "provided",
    "com.github.mpilquist" %% "simulacrum"    % simulacrumVersion % "provided",
    compilerPlugin("org.scalamacros" % "paradise" % macroParadiseVersion cross CrossVersion.full)
  ),
  coverageExcludedPackages := "tabulate\\.laws\\..*",
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
  .aggregate(core, cats, scalaz, scalazStream, laws, tests, docs, generic, benchmark, jackson, commons, opencsv)

lazy val core = project
  .settings(
    moduleName := "tabulate",
    name       := "core"
  )
  .settings(allSettings: _*)

lazy val jackson = project
  .settings(
      moduleName := "tabulate-jackson",
      name       := "jackson"
    )
    .settings(libraryDependencies ++= Seq(
      "com.fasterxml.jackson.dataformat" %  "jackson-dataformat-csv" % jacksonCsvVersion,
      "org.scalatest"                    %% "scalatest"              % scalatestVersion % "test"
    ))
    .settings(allSettings: _*)
    .dependsOn(core, laws % "test")

lazy val commons = project
  .settings(
      moduleName := "tabulate-commons",
      name       := "commons"
    )
    .settings(libraryDependencies ++= Seq(
      "org.apache.commons" %  "commons-csv" % commonsCsvVersion,
      "org.scalatest"      %% "scalatest"   % scalatestVersion % "test"
    ))
    .settings(allSettings: _*)
    .dependsOn(core, laws % "test")

lazy val opencsv = project
  .settings(
      moduleName := "tabulate-opencsv",
      name       := "opencsv"
    )
    .settings(libraryDependencies ++= Seq(
      "com.opencsv"   %  "opencsv"   % opencsvVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    ))
    .settings(allSettings: _*)
    .dependsOn(core, laws % "test")

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
  .settings(allSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.chuusai"                %% "shapeless"            % shapelessVersion,
    "org.scalatest"              %% "scalatest"            % scalatestVersion           % "test",
    "com.github.alexarchambault" %% "scalacheck-shapeless" % scalacheckShapelessVersion % "test"  exclude("com.chuusai", "shapeless_2.10.4")
  ))
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
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")
  .dependsOn(core, laws % "test")

lazy val benchmark = project
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .enablePlugins(JmhPlugin)
  .settings(libraryDependencies ++= Seq(
    "com.github.marklister"            %% "product-collections"    % productCollectionVersion,
    "com.univocity"                    %  "univocity-parsers"      % univocityVersion,
    "org.scalatest"                    %% "scalatest"              % scalatestVersion % "test"
  ))
  .dependsOn(core, jackson, opencsv, commons)

lazy val docs = project
  .settings(allSettings: _*)
  .settings(site.settings: _*)
  .settings(ghpages.settings: _*)
  .settings(unidocSettings: _*)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmark),
    autoAPIMappings := true,
    apiURL := Some(url("http://nrinaudo.github.io/tabulate/api/")),
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    )
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

addCommandAlias("runBench",    "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias("runProfiler", "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 tabulate.benchmark.*tabulate.*")
