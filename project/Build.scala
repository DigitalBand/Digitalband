import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Digitalband"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "mysql" % "mysql-connector-java" % "5.1.22",
    "org.jsoup" % "jsoup" % "1.6.3",
    "com.typesafe" %% "play-plugins-mailer" % "2.1-SNAPSHOT"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" / "bootstrap" ** "main.less")
  )

}
