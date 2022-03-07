package fr.inrae.metabolomics.p2m2.command

import utest.{TestSuite, Tests, test}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

object MassLynx2IsocorCommandTest extends TestSuite {

  val tests = Tests {

    test("GCMS2IsocorCommandTest - help") {
      Try(MassLynx2IsocorCommand.main(Array(""))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("GCMS2IsocorCommandTest - with args") {
      val tp = File.createTempFile("out-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,"--out",tp))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("GCMS2IsocorCommandTest - with args derivatives") {
      val tp = File.createTempFile("out-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(
          getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,
          "--out",tp,
          "--derivatives",getClass.getResource("/MassLynx/correspondence_derivatives.txt").getPath))) match {
        case Success(_) => {
          Source.fromFile(getClass.getResource("/MassLynx/correspondence_derivatives.txt").getPath)
            .getLines()
            //.filter(x => x.contains("GlyN15_A_1"))
            .map(x => println(x))
        }
        case Failure(_) => assert(false)
      }
    }

  }
}
