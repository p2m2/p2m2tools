scalaVersion := "2.13.6"

name := "P2M2Tools"
organizationName := "p2m2"
organization := "com.github.p2m2"
organizationHomepage := Some(url("https://www6.inrae.fr/p2m2"))
licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))
homepage := Some(url("https://github.com/p2m2/p2m2tools"))
description := "Development of it and bioinformatics tools for the activities of the P2M2 platform."
scmInfo := Some(
  ScmInfo(
    url("https://github.com/p2m2/p2m2tools"),
    "scm:git@github.com:p2m2/p2m2tools.git"
  )
)

versionScheme := Some("early-semver")

val static_version      = "0.1.7"
val version_build = scala.util.Properties.envOrElse("PROG_VERSION", static_version )

version :=  version_build

libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.11" % "test"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"

// Coverage

coverageMinimumStmtTotal := 97
coverageMinimumBranchTotal := 93
coverageMinimumStmtPerPackage := 93
coverageMinimumBranchPerPackage := 93
coverageMinimumStmtPerFile := 93
coverageMinimumBranchPerFile := 93
coverageFailOnMinimum := true
coverageHighlighting := true

// Publication

developers := List(
  Developer("ofilangi", "Olivier Filangi", "olivier.filangi@inrae.fr",url("https://github.com/ofilangi"))
)

credentials += {

  val realm = scala.util.Properties.envOrElse("REALM_CREDENTIAL", "" )
  val host = scala.util.Properties.envOrElse("HOST_CREDENTIAL", "" )
  val login = scala.util.Properties.envOrElse("LOGIN_CREDENTIAL", "" )
  val pass = scala.util.Properties.envOrElse("PASSWORD_CREDENTIAL", "" )

  val file_credential = Path.userHome / ".sbt" / ".credentials"

  if (reflect.io.File(file_credential).exists) {
    Credentials(file_credential)
  } else {
    Credentials(realm,host,login,pass)
  }
}

publishTo := {
  if (isSnapshot.value)
    Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
pomIncludeRepository := { _ => false }
publishMavenStyle := true

assembly / target := file("assembly")

testFrameworks += new TestFramework("utest.runner.Framework")
Global / onChangedBuildSource := ReloadOnSourceChanges
