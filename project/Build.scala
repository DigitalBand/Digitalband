import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "Digitalband"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "mysql" % "mysql-connector-java" % "5.1.22",
    "org.jsoup" % "jsoup" % "1.6.3",
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
    "com.tzavellas" % "sse-guice" % "0.7.0",
    "com.typesafe.slick" %% "slick" % "1.0.0",
    "org.ocpsoft.prettytime" % "prettytime" % "2.1.3.Final",
    "commons-codec" % "commons-codec" % "1.7",
    "commons-io" % "commons-io" % "2.4",
    "ch.qos.logback" % "logback-classic" % "1.0.9"
  )

  def customLessEntryPoints(base: File): PathFinder = (
      (base / "app" / "assets" / "stylesheets" / "bootstrap" ** "main.less") +++
      (base / "app" / "assets" / "stylesheets" ** "site.less") +++
      (base / "app" / "assets" / "stylesheets" ** "fileupload.less")
    )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    lessEntryPoints <<= baseDirectory(customLessEntryPoints)
  )

}
