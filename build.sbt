import java.nio.file.Paths

name := "digitalband"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

resolvers += Resolver.file("Local repo", file(Paths.get(System.getProperty("user.home"), ".ivy2", "local").toString))(Resolver.ivyStylePatterns)


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "org.ocpsoft.prettytime" % "prettytime" % "2.1.3.Final",
  "commons-codec" % "commons-codec" % "1.7",
  "commons-io" % "commons-io" % "2.4",
  "web.common" %% "web-common" % "0.1-SNAPSHOT",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.yuvimasory" % "jerkson_2.10" % "0.6.1"
)

includeFilter in (Assets, LessKeys.less) := "main.less"
