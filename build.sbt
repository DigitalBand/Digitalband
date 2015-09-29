import java.nio.file.Paths

name := "digitalband"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

resolvers += Resolver.file("Local repo", file(Paths.get(System.getProperty("user.home"), ".ivy2", "local").toString))(Resolver.ivyStylePatterns)


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.slick" %% "slick" % "3.1.0-RC2",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "org.ocpsoft.prettytime" % "prettytime" % "2.1.3.Final",
  "commons-codec" % "commons-codec" % "1.7",
  "commons-io" % "commons-io" % "2.4",
  "web.common" %% "web-common" % "0.1.3",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.yuvimasory" % "jerkson_2.10" % "0.6.1",
  "org.webjars" % "ckeditor" % "4.4.6",
  "org.webjars" % "font-awesome" % "4.3.0-1",
  "org.webjars" % "highcharts" % "4.0.4",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "jquery.lazyload" % "1.9.3",
  "org.webjars" % "Magnific-Popup" % "0.9.9",
  "org.webjars" % "bootstrap" % "3.3.2",
  "org.webjars" % "angularjs" % "1.2.28",
  "org.webjars" % "angular-ui-router" % "0.2.13",
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0",
  "org.webjars" % "ng-ckeditor" % "0.2"
)

includeFilter in (Assets, LessKeys.less) := "main.less"
