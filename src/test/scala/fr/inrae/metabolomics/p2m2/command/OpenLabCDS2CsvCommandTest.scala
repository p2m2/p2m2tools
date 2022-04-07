package fr.inrae.metabolomics.p2m2.command

import utest.{TestSuite, Tests, test}

import java.io.File
import scala.util.{Failure, Success, Try}
object OpenLabCDS2CsvCommandTest extends TestSuite {

  val tests = Tests {

    test("OpenLabCDS2CsvCommand - help") {
      Try(OpenLabCDS2CsvCommand.main(Array(""))) match {
        case Success(_) => assert(true)
        case Failure(_) => assert(false)
      }
    }

    test("OpenLabCDS2CsvCommand - with args") {
      val infiles = Array(
        getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath,
        getClass.getResource("/OpenLabCDS/Report_Ex2.txt").getPath,
        getClass.getResource("/OpenLabCDS/Report_Ex3.txt").getPath
      )
      val tp = File.createTempFile("out-", ".csv").getPath

      Try(OpenLabCDS2CsvCommand.main(infiles ++ Array("--out",tp))) match {
        case Success(a) => println(a);assert(true)
        case Failure(_) => assert(false)
      }
    }

  }
}
