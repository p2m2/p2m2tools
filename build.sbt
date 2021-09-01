name := "P2M2WorkflowsTools"
version := "0.1"
scalaVersion := "2.13.6"
libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.10" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.0.0"

testFrameworks += new TestFramework("utest.runner.Framework")
Global / onChangedBuildSource := ReloadOnSourceChanges