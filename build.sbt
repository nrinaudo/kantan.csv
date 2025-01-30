ThisBuild / kantanProject := "csv"
ThisBuild / startYear     := Some(2015)

lazy val jsModules: Seq[ProjectReference] = Seq(
  catsJS,
  coreJS,
  enumeratumJS,
  genericJS,
  lawsJS,
  refinedJS,
  scalazJS
)

lazy val jvmModules: Seq[ProjectReference] = Seq(
  benchmark,
  catsJVM,
  commons,
  coreJVM,
  enumeratumJVM,
  genericJVM,
  jackson,
  java8,
  lawsJVM,
  libra,
  refinedJVM,
  scalazJVM
)

// - root projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val root = Project(id = "kantan-csv", base = file("."))
  .settings(moduleName := "root")
  .enablePlugins(UnpublishedPlugin)
  .settings(
    console / initialCommands :=
      """
      |import kantan.csv._
      |import kantan.csv.ops._
      |import kantan.csv.generic._
      |import kantan.csv.refined._
    """.stripMargin
  )
  .aggregate((jsModules ++ jvmModules :+ (docs: ProjectReference)): _*)
  .dependsOn(coreJVM, genericJVM, libra, refinedJVM, enumeratumJVM)

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .settings(name := "docs")
  .settings(
    ScalaUnidoc / unidoc / unidocProjectFilter :=
      inAnyProject -- inProjects(benchmark) -- inProjects(jsModules: _*)
  )
  .settings(libraryDependencies += "joda-time" % "joda-time" % Versions.jodaTime)
  .dependsOn(
    coreJVM,
    java8,
    lawsJVM,
    libra,
    catsJVM,
    scalazJVM,
    genericJVM,
    jackson,
    commons,
    refinedJVM,
    enumeratumJVM
  )

lazy val benchmark = project
  .enablePlugins(UnpublishedPlugin, JmhPlugin)
  .dependsOn(coreJVM, jackson, commons, lawsJVM % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.opencsv"          % "opencsv"           % Versions.opencsv,
      "com.univocity"        % "univocity-parsers" % Versions.univocity,
      "com.github.tototoshi" %% "scala-csv"        % Versions.scalaCsv
    )
  )

// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = kantanCrossProject("core")
  .settings(moduleName := "kantan.csv")
  .enablePlugins(PublishedPlugin, BoilerplatePlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs" % Versions.kantanCodecs
    )
  )
  .laws("laws")

lazy val coreJVM = core.jvm
lazy val coreJS  = core.js

lazy val laws = kantanCrossProject("laws")
  .settings(moduleName := "kantan.csv-laws")
  .enablePlugins(PublishedPlugin, BoilerplatePlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %%% "kantan.codecs-laws" % Versions.kantanCodecs)

lazy val lawsJVM = laws.jvm
lazy val lawsJS  = laws.js

// - external engines projects -----------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jackson = project
  .settings(moduleName := "kantan.csv-jackson")
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % Versions.jacksonCsv
    )
  )

lazy val commons = project
  .settings(moduleName := "kantan.csv-commons")
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % Test)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.commons"     % "commons-csv"              % Versions.commonsCsv,
      "org.scala-lang.modules" %% "scala-collection-compat" % Versions.collectionCompat
    )
  )

// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = kantanCrossProject("generic")
  .settings(moduleName := "kantan.csv-generic")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs-shapeless"      % Versions.kantanCodecs,
      "com.nrinaudo" %%% "kantan.codecs-shapeless-laws" % Versions.kantanCodecs % Test
    )
  )

lazy val genericJVM = generic.jvm
lazy val genericJS  = generic.js

// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = kantanCrossProject("scalaz")
  .settings(moduleName := "kantan.csv-scalaz")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs-scalaz"      % Versions.kantanCodecs,
      "com.nrinaudo" %%% "kantan.codecs-scalaz-laws" % Versions.kantanCodecs % Test
    )
  )

lazy val scalazJVM = scalaz.jvm
lazy val scalazJS  = scalaz.js

// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = kantanCrossProject("cats")
  .settings(moduleName := "kantan.csv-cats")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs-cats"      % Versions.kantanCodecs,
      "com.nrinaudo" %%% "kantan.codecs-cats-laws" % Versions.kantanCodecs % Test
    )
  )

lazy val catsJVM = cats.jvm
lazy val catsJS  = cats.js

// - java8 projects ----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val java8 = project
  .settings(
    moduleName := "kantan.csv-java8",
    name       := "java8"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-java8"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-java8-laws" % Versions.kantanCodecs % "test"
    )
  )

// - refined project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val refined = kantanCrossProject("refined")
  .settings(moduleName := "kantan.csv-refined")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs-refined"      % Versions.kantanCodecs,
      "com.nrinaudo" %%% "kantan.codecs-refined-laws" % Versions.kantanCodecs % Test
    )
  )

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js

// - enumeratum project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val enumeratum = kantanCrossProject("enumeratum")
  .settings(moduleName := "kantan.csv-enumeratum")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %%% "kantan.codecs-enumeratum"      % Versions.kantanCodecs,
      "com.nrinaudo" %%% "kantan.codecs-enumeratum-laws" % Versions.kantanCodecs % Test
    )
  )

lazy val enumeratumJVM = enumeratum.jvm
lazy val enumeratumJS  = enumeratum.js

// - libra project -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val libra = project
  .settings(
    moduleName := "kantan.csv-libra",
    name       := "libra"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % Test)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-libra"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-libra-laws" % Versions.kantanCodecs % Test
    )
  )

// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias(
  "runProfiler",
  "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof \"stack:detailLine=true;lines=5;period=1\" kantan.csv.benchmark.*kantan.*"
)
