import sbt._
import Keys._

object JdubextBuild extends Build
{
  lazy val core = Project("core", file("./"), settings = Defaults.defaultSettings ++ Seq(
    organization:= "com.github.ornicar",
    name := "jdubext",
    version := "1.12",
    scalaVersion := "2.9.1",
    libraryDependencies := Seq(
      "com.codahale" %% "jdub" % "0.0.6",
      "joda-time" % "joda-time" % "2.0",
      "org.joda" % "joda-convert" % "1.2",
      "org.specs2" %% "specs2" % "1.8.2",
      "org.scalaz" %% "scalaz-core" % "6.0.4"
    ),
    //libraryDependencies in Test += "org.specs2" %% "specs2" % "1.8.2",
    //resolvers := Seq(codahale, typesafe, iliaz),
    scalacOptions := Seq("-deprecation", "-unchecked"),
    publishTo := Some(Resolver.sftp(
      "iliaz",
      "scala.iliaz.com"
    ) as ("scala_iliaz_com", Path.userHome / ".ssh" / "id_rsa"))
  ))
}
