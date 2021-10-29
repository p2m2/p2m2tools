name := "P2M2WorkflowsTools"
version := "0.1.4"
scalaVersion := "2.13.6"

libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.10" % "test"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"

coverageMinimumStmtTotal := 70
coverageFailOnMinimum := false
coverageHighlighting := true

testFrameworks += new TestFramework("utest.runner.Framework")
Global / onChangedBuildSource := ReloadOnSourceChanges
