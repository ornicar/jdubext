import sbt._
import Keys._

object JdubextBuild extends Build
{
  lazy val core = Project("core", file("./")) settings(
    organization:= "com.github.ornicar",
    name := "jdubext",
    version := "1.2",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "com.codahale" %% "jdub" % "0.0.6",
      "org.specs2" %% "specs2" % "1.7.1"
    ),
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    publishTo := Some(Resolver.sftp(
      "iliaz",
      "scala.iliaz.com"
    ) as ("scala_iliaz_com", Path.userHome / ".ssh" / "id_rsa"))
  )
}
