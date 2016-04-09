import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._

val kantanCodecsVersion        = "0.1.2"
val catsVersion                = "0.4.1"
val macroParadiseVersion       = "2.1.0"
val scalaCheckVersion          = "1.12.5"
val disciplineVersion          = "0.4"
val shapelessVersion           = "2.3.0"
val scalatestVersion           = "3.0.0-M9"
val scalazVersion              = "7.2.0"
val scalazStreamVersion        = "0.8"
val productCollectionVersion   = "1.4.3"
val opencsvVersion             = "3.7"
val univocityVersion           = "2.0.2"
val jacksonCsvVersion          = "2.7.3"
val commonsCsvVersion          = "1.2"
val scalacheckShapelessVersion = "1.12.1"
val jodaVersion                = "2.9.3"
val jodaConvertVersion         = "1.8.1"
val scalaCsvVersion            = "1.3.0"
val kindProjectorVersion       = "0.7.1"

lazy val buildSettings = Seq(
  organization       := "com.nrinaudo",
  scalaVersion       := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  autoAPIMappings    := true
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
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  coverageExcludedPackages := "kantan\\.csv\\.laws\\..*",
  incOptions := incOptions.value.withNameHashing(true)
)

lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://nrinaudo.github.io/kantan.csv/")),
  licenses := Seq("MIT License" → url("http://www.opensource.org/licenses/mit-license.php")),
  apiURL := Some(url("https://nrinaudo.github.io/kantan.csv/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nrinaudo/kantan.csv"),
      "scm:git:git@github.com:nrinaudo/kantan.csv.git"
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

lazy val root = Project(id = "kantan-csv", base = file("."))
  .settings(moduleName := "root")
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    initialCommands in console :=
    """
      |import kantan.csv._
      |import kantan.csv.ops._
      |import kantan.csv.generic._
      |import kantan.csv.joda.time._
    """.stripMargin
  )
  .aggregate(core, cats, scalaz, scalazStream, laws, tests, docs, generic, benchmark, jackson, commons, opencsv, jodaTime)
  .dependsOn(core, generic, jodaTime)

lazy val core = project
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(
    moduleName := "kantan.csv",
    name       := "core"
  )
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs" % kantanCodecsVersion)
  .settings(allSettings: _*)

lazy val jackson = project
  .settings(
      moduleName := "kantan.csv-jackson",
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
      moduleName := "kantan.csv-commons",
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
      moduleName := "kantan.csv-opencsv",
      name       := "opencsv"
    )
    .settings(libraryDependencies ++= Seq(
      "com.opencsv"   %  "opencsv"   % opencsvVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    ))
    .settings(allSettings: _*)
    .dependsOn(core, laws % "test")

lazy val laws = project
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(
    moduleName := "kantan.csv-laws",
    name       := "laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck"        % scalaCheckVersion,
    "org.typelevel"  %% "discipline"        % disciplineVersion,
    "com.nrinaudo"  %% "kantan.codecs-laws" % kantanCodecsVersion
  ))
  .settings(allSettings: _*)
  .dependsOn(core)

lazy val generic = project
  .settings(
    moduleName := "kantan.csv-generic",
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
    moduleName := "kantan.csv-scalaz",
    name       := "scalaz"
  )
  .settings(allSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-scalaz"      % kantanCodecsVersion,
    "com.nrinaudo"  %% "kantan.codecs-scalaz-laws" % kantanCodecsVersion % "test",
    "org.scalatest" %% "scalatest"                 % scalatestVersion    % "test"
  ))
  .dependsOn(core, laws % "test")

lazy val scalazStream = Project(id = "scalaz-stream", base = file("scalaz-stream"))
  .settings(
    moduleName := "kantan.csv-scalaz-stream",
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
    moduleName := "kantan.csv-cats",
    name       := "cats"
  )
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-cats"      % kantanCodecsVersion,
    "com.nrinaudo"  %% "kantan.codecs-cats-laws" % kantanCodecsVersion % "test",
    "org.scalatest" %% "scalatest"               % scalatestVersion    % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws % "test")

lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-joda-time"      % kantanCodecsVersion,
    "com.nrinaudo"  %% "kantan.codecs-joda-time-laws" % kantanCodecsVersion % "test",
    "org.scalatest" %% "scalatest"                    % scalatestVersion    % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws % "test")

lazy val tests = project
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")
  .dependsOn(core, laws % "test")

lazy val benchmark = project
  .settings(buildSettings: _*)
  .settings(baseSettings: _*)
  .settings(noPublishSettings: _*)
  .enablePlugins(JmhPlugin)
  .settings(libraryDependencies ++= Seq(
    "com.github.marklister" %% "product-collections"    % productCollectionVersion,
    "com.univocity"         %  "univocity-parsers"      % univocityVersion,
    "com.github.tototoshi"  %% "scala-csv"              % scalaCsvVersion,
    "org.scalatest"         %% "scalatest"              % scalatestVersion % "test"
  ))
  .dependsOn(core, jackson, opencsv, commons)

lazy val docs = project
  .settings(allSettings: _*)
  .settings(site.settings: _*)
  .settings(site.preprocessSite(): _*)
  .settings(ghpages.settings: _*)
  .settings(unidocSettings: _*)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmark),
    apiURL := Some(url("http://nrinaudo.github.io/kantan.csv/api/")),
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    )
  )
  .settings(libraryDependencies ++= Seq(
    "joda-time" % "joda-time"    % jodaVersion,
    "org.joda"  % "joda-convert" % jodaConvertVersion
  ))
  .settings(tutSettings: _*)
  .settings(
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api"),
    site.addMappingsToSiteDir(tut, "_tut"),
    git.remoteRepo := "git@github.com:nrinaudo/kantan.csv.git",
    ghpagesNoJekyll := false,
    includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" |
                                 "*.eot" | "*.svg" | "*.ttf" | "*.woff" | "*.woff2" | "*.otf"
  )
  .settings(noPublishSettings:_*)
  .dependsOn(core, scalazStream, laws, cats, scalaz, generic, jackson, commons, opencsv)

addCommandAlias("runBench",    "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias("runProfiler", "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*")
