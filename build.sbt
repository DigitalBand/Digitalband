import sbt._
import Keys._
import java.nio.file.Paths
import play.Project._

playScalaSettings

name := "digitalband"

version := "1.0-SNAPSHOT"

resolvers += Resolver.file("Local repo", file(Paths.get(System.getProperty("user.home"), ".ivy2", "local").toString))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.typesafe.slick" % "slick_2.10" % "2.0.1-RC1",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "org.ocpsoft.prettytime" % "prettytime" % "2.1.3.Final",
  "commons-codec" % "commons-codec" % "1.7",
  "commons-io" % "commons-io" % "2.4",
  "wt.common" %% "wassertim-common" % "0.1-SNAPSHOT",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.yuvimasory" % "jerkson_2.10" % "0.6.1"
)

def customLessEntryPoints(base: File): PathFinder = (base / "app" / "assets" / "stylesheets" ** "main.less")

lessEntryPoints <<= baseDirectory(customLessEntryPoints)