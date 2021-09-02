name := "P2M2WorkflowsTools"
version := "0.1"
scalaVersion := "2.13.6"
libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.10" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")
Global / onChangedBuildSource := ReloadOnSourceChanges