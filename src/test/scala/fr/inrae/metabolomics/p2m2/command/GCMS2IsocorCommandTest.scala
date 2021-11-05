package fr.inrae.metabolomics.p2m2.command

import utest.{TestSuite, Tests, test}

import java.io.File
import scala.util.{Failure, Success, Try}
object GCMS2IsocorCommandTest extends TestSuite {

  val tests = Tests {

    test("GCMS2IsocorCommandTest - help") {
      Try(GCMS2IsocorCommand.main(Array(""))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("GCMS2IsocorCommandTest - with args") {
      val tp = File.createTempFile("out-", ".tsv").getPath

      Try(GCMS2IsocorCommand.main(Array(getClass.getResource("/GCMS/13CPROT1.txt").getPath,"--out",tp))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

  }
}
