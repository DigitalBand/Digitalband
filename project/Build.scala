import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Digitalband"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    "mysql" % "mysql-connector-java" % "5.1.22",
    "org.jsoup" % "jsoup" % "1.6.3",
    "com.typesafe" %% "play-plugins-mailer" % "2.1-SNAPSHOT",
    "com.tzavellas" % "sse-guice" % "0.7.0",
    "com.typesafe" % "slick_2.10" % "1.0.0-RC1",
    "ch.qos.logback" % "logback-classic" % "1.0.9"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" / "bootstrap" ** "main.less")
  )

}
