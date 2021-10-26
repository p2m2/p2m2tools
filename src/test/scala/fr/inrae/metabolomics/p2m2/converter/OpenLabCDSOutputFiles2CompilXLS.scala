package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField
import utest.{TestSuite, Tests, test}

import scala.io.Source

object OpenLabCDSOutputFiles2CompilXLS extends TestSuite {
  val tests = Tests {

    test("transform - nothing") {
      val entry = OutputOpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Sample_Name -> "Sample Name 1"
        )
      )
      assert(OpenLabCDS2CompilXLS().transform(entry,List(),"") == List())
    }

    test("transform - nothing") {
      val entry = OutputOpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Sample_Name -> "Sample Name 1",
        ),
        results = List(
          Map(
            "Name_bad_" -> "CompoundX",
            "X" -> "5",
            "Y" -> "6"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert(OpenLabCDS2CompilXLS().transform(entry, List("CompoundX"),"X") == List(None))
    }

    test("transform - ok 1") {
      val entry = OutputOpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Sample_Name -> "Sample Name 1",
        ),
        results = List(
          Map(
            "Name" -> "CompoundX",
            "X" -> "5",
            "Y" -> "6"
          ),
          Map(
            "Name" -> "CompoundY",
            "X" -> "8",
            "Y" -> "9"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert(OpenLabCDS2CompilXLS().transform(entry,List("CompoundX","CompoundY","badCompound"),"X")
        == List(Some("5"),Some("8"),None))
      assert(OpenLabCDS2CompilXLS().transform(entry,List("CompoundX","badCompound","CompoundY"),"X")
        == List(Some("5"),None,Some("8")))
    }

    test("transform - Report.txt") {
      val r : String = getClass.getResource("/Report.txt").getPath
      println(r)
      println("----")
      println(Source.fromFile(r).mkString.length)
      val openlabs_output = OpenLabCDSParser.parse(getClass.getResource("/Report.txt").getPath)
      assert( OpenLabCDS2CompilXLS().transform(openlabs_output,List("Glyoxylate","Xylose2","CompoundY"),"Area").length == 2 )
    }
  }
}
