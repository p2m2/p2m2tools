package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.format.GCMS
import fr.inrae.metabolomics.p2m2.parser.GCMSParser
import GCMS.{HeaderField, HeaderFileField}
import utest.{TestSuite, Tests, test}

import scala.util.{Success, Try}

object GCMSOutputFiles2IsocorInputTest extends TestSuite {
  val tests: Tests = Tests {

    test("transform - nothing") {
      val entry = GCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.Data_File_Name -> "Date File Name"
        )
      )
      assert( GCMSOutputFiles2IsocorInput().transform(entry) == List() )
    }

    test("transform - missing Data_File_Name") {
      val entry = GCMS(
        origin = "file/SAMPLE",
        header = Map(
        )
      )
      assert(Try( GCMSOutputFiles2IsocorInput().transform(entry) == List() ).isFailure)
    }

    test("transform - Name missing") {
      val entry = GCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.Data_File_Name -> "Date File Name",
        ),
        ms_quantitative_results = List(
          Map(
            HeaderField.`ID#` -> "1",
            HeaderField.Area -> "area1"
          )
        )
      )
      assert(Try( GCMSOutputFiles2IsocorInput().transform(entry) == List() ).isFailure)
    }

    test("transform - nothing 2") {
      val entry = GCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.Data_File_Name -> "Date File Name",
        ),
        ms_quantitative_results = List(
          Map(
            HeaderField.`ID#` -> "1",
            HeaderField.Name -> "name1",
            HeaderField.Area -> "area1"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert( GCMSOutputFiles2IsocorInput().transform(entry).isEmpty )
    }

    test("transform - nothing") {
      val entry = GCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.Data_File_Name -> "Date File Name",
        ),
        ms_quantitative_results = List(
          Map(
            HeaderField.Name -> "metabolite1_derivative1_isotopologue1",
            HeaderField.Area -> "area1"
          )
        )
      )
      val resolution = scala.util.Random.nextInt(10000)

      assert (
         GCMSOutputFiles2IsocorInput(resolution).transform(entry)  ==
           List(
             s"Date File Name\tmetabolite1\tderivative1\tisotopologue1\tarea1\t$resolution"
           )
       )
    }

    test("transform - 13CPROT1.txt") {
      val gcms_output = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT1.txt").getPath)
      assert( GCMSOutputFiles2IsocorInput().transform(gcms_output).length == 247 )
    }

    test("transform - 13CPROT2.txt") {
      val gcms_output = GCMSParser.parse(getClass.getResource("/GCMS/13CPROT2.txt").getPath)
      println("======================================================================")
      println(GCMSOutputFiles2IsocorInput().transform(gcms_output).length)

      assert(  GCMSOutputFiles2IsocorInput().transform(gcms_output).length == 254 )
    }
  }
}
