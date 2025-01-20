ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "plant-lab",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M40",
      "org.http4s" %% "http4s-dsl"         % "1.0.0-M40",
      "org.http4s" %% "http4s-circe"       % "1.0.0-M40",
      "io.circe"   %% "circe-generic"      % "0.14.5",
      "org.typelevel" %% "cats-effect"     % "3.5.4",
      "org.typelevel" %% "log4cats-slf4j"   % "2.7.0"
    )
  )
