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
      "org.typelevel" %% "log4cats-slf4j"   % "2.7.0",
      "org.scalikejdbc" %% "scalikejdbc"       % "4.3.2",
      "org.scalikejdbc" %% "scalikejdbc-config"  % "4.3.2",
      "org.postgresql" % "postgresql" % "42.7.5",
      // why only hte single percent? idk
      "com.h2database"  %  "h2"                % "2.2.224",
      "ch.qos.logback"  %  "logback-classic"   % "1.5.6"
    ),
    mainClass := Some("dev.whalenet.plant_lab.Main"),
    // needed to avoid merge conflicts when assembling
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _ @ _*) => MergeStrategy.discard // Discard metadata files
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard // Ignore MANIFEST files
      case PathList("reference.conf") => MergeStrategy.concat // Merge configuration files
      case PathList("application.conf") => MergeStrategy.concat // Merge application configs
      case PathList("logback.xml") => MergeStrategy.first // Keep the first logback.xml
      case x => MergeStrategy.first // Default to the first file
    }

  )
