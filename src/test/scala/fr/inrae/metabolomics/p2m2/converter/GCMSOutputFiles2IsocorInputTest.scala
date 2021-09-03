package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.GCMSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputGCMS.HeaderField
import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object GCMSOutputFiles2IsocorInputTest extends TestSuite {
  val tests = Tests {
    /*
    test("test1"){
      val paths = Array(
        getClass.getResource("/13CPROT1.txt").getPath,
        getClass.getResource("/13CPROT2.txt").getPath,
        getClass.getResource("/13CPROT3.txt").getPath,
        getClass.getResource("/13CPROT4.txt").getPath)

      GCMS2Isocor(paths)
    }*/
    test("transform - nothing") {
      val entry = OutputGCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Data_File_Name -> "Date File Name"
        )
      )
      assert( GCMSOutputFiles2IsocorInput(2000).transform(entry) == List() )
    }

    test("transform - nothing") {
      val entry = OutputGCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Data_File_Name -> "Date File Name",
        ),
        ms_quantitative_results = List(
          Map(
            "ID#" -> "1",
            "Name" -> "name1",
            "Area" -> "area1"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert( GCMSOutputFiles2IsocorInput(2000).transform(entry).length == 0 )
    }

    test("transform - nothing") {
      val entry = OutputGCMS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Data_File_Name -> "Date File Name",
        ),
        ms_quantitative_results = List(
          Map(
            "Name" -> "metabolite1_derivative1_isotopologue1",
            "Area" -> "area1"
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
      val gcms_output = GCMSParser.parse(getClass.getResource("/13CPROT1.txt").getPath)
      assert( GCMSOutputFiles2IsocorInput(2000).transform(gcms_output).length == 259 )
    }

    test("transform - 13CPROT2.txt") {
      val gcms_output = GCMSParser.parse(getClass.getResource("/13CPROT2.txt").getPath)
      assert(  GCMSOutputFiles2IsocorInput(2000).transform(gcms_output).length == 259 )
    }
  }
}
