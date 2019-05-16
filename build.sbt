kantanProject in ThisBuild := "csv"
startYear in ThisBuild     := Some(2015)

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
  jodaTime,
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
    initialCommands in console :=
      """
      |import kantan.csv._
      |import kantan.csv.ops._
      |import kantan.csv.generic._
      |import kantan.csv.joda.time._
      |import kantan.csv.refined._
    """.stripMargin
  )
  .aggregate((jsModules ++ jvmModules :+ (docs: ProjectReference)): _*)
  .aggregateIf(java8Supported)(java8)
  .dependsOn(coreJVM, genericJVM, jodaTime, libra, refinedJVM, enumeratumJVM)

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .settings(name := "docs")
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) :=
      inAnyProject -- inProjectsIf(!java8Supported)(java8) -- inProjects(benchmark) -- inProjects(jsModules: _*)
  )
  .dependsOn(
    coreJVM,
    lawsJVM,
    libra,
    catsJVM,
    scalazJVM,
    genericJVM,
    jackson,
    commons,
    jodaTime,
    refinedJVM,
    enumeratumJVM
  )
  .dependsOnIf(java8Supported)(java8)

lazy val benchmark = project
  .enablePlugins(UnpublishedPlugin, JmhPlugin)
  .dependsOn(coreJVM, jackson, commons)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.marklister" %% "product-collections" % Versions.productCollection,
      "com.opencsv"           % "opencsv"              % Versions.opencsv,
      "com.univocity"         % "univocity-parsers"    % Versions.univocity,
      "com.github.tototoshi"  %% "scala-csv"           % Versions.scalaCsv,
      "org.scalatest"         %% "scalatest"           % Versions.scalatest % "test"
    )
  )

// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = kantanCrossProject("core")
  .settings(moduleName := "kantan.csv")
  .enablePlugins(PublishedPlugin, BoilerplatePlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs" % Versions.kantanCodecs,
      "org.scalatest" %%% "scalatest"     % Versions.scalatest % "test"
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
  .dependsOn(coreJVM, lawsJVM % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % Versions.jacksonCsv,
      "org.scalatest"                    %% "scalatest"             % Versions.scalatest % "test"
    )
  )

lazy val commons = project
  .settings(moduleName := "kantan.csv-commons")
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % "test")
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-csv" % Versions.commonsCsv,
      "org.scalatest"      %% "scalatest"  % Versions.scalatest % "test"
    )
  )

// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = kantanCrossProject("generic")
  .settings(moduleName := "kantan.csv-generic")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs-shapeless"      % Versions.kantanCodecs,
      "com.nrinaudo"  %%% "kantan.codecs-shapeless-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %%% "scalatest"                    % Versions.scalatest % "test"
    )
  )

lazy val genericJVM = generic.jvm
lazy val genericJS  = generic.js

// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = kantanCrossProject("scalaz")
  .settings(moduleName := "kantan.csv-scalaz")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs-scalaz"      % Versions.kantanCodecs,
      "com.nrinaudo"  %%% "kantan.codecs-scalaz-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %%% "scalatest"                 % Versions.scalatest % "test"
    )
  )

lazy val scalazJVM = scalaz.jvm
lazy val scalazJS  = scalaz.js

// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = kantanCrossProject("cats")
  .settings(moduleName := "kantan.csv-cats")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs-cats"      % Versions.kantanCodecs,
      "com.nrinaudo"  %%% "kantan.codecs-cats-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %%% "scalatest"               % Versions.scalatest % "test"
    )
  )

lazy val catsJVM = cats.jvm
lazy val catsJS  = cats.js

// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(coreJVM, lawsJVM % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %% "kantan.codecs-joda-time"      % Versions.kantanCodecs,
      "com.nrinaudo"  %% "kantan.codecs-joda-time-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %% "scalatest"                    % Versions.scalatest % "test"
    )
  )

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
      "com.nrinaudo"  %% "kantan.codecs-java8"      % Versions.kantanCodecs,
      "com.nrinaudo"  %% "kantan.codecs-java8-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %% "scalatest"                % Versions.scalatest % "test"
    )
  )

// - refined project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val refined = kantanCrossProject("refined")
  .settings(moduleName := "kantan.csv-refined")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs-refined"      % Versions.kantanCodecs,
      "com.nrinaudo"  %%% "kantan.codecs-refined-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %%% "scalatest"                  % Versions.scalatest % "test"
    )
  )

lazy val refinedJVM = refined.jvm
lazy val refinedJS  = refined.js

// - enumeratum project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val enumeratum = kantanCrossProject("enumeratum")
  .settings(moduleName := "kantan.csv-enumeratum")
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %%% "kantan.codecs-enumeratum"      % Versions.kantanCodecs,
      "com.nrinaudo"  %%% "kantan.codecs-enumeratum-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %%% "scalatest"                     % Versions.scalatest % "test"
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
  .dependsOn(coreJVM, lawsJVM % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo"  %% "kantan.codecs-libra"      % Versions.kantanCodecs,
      "com.nrinaudo"  %% "kantan.codecs-libra-laws" % Versions.kantanCodecs % "test",
      "org.scalatest" %% "scalatest"                % Versions.scalatest % "test"
    )
  )

// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias(
  "runProfiler",
  "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*"
)
