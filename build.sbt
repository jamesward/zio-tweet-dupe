name := "zio-flaky"

scalaVersion := "3.1.1-RC1-bin-20210927-3f978b3-NIGHTLY"

val zioVersion = "1.0.12"

scalacOptions += "-language:experimental.fewerBraces"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-streams"  % zioVersion,
  "dev.zio" %% "zio-json"     % "0.2.0-M1",

  "org.apache.commons" % "commons-text" % "1.9",

  "dev.zio" %% "zio-test"     % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
