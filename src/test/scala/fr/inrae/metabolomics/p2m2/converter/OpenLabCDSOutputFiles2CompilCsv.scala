package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputOpenLabCDS.HeaderField
import utest.{TestSuite, Tests, test}

object OpenLabCDSOutputFiles2CompilCsv extends TestSuite {
  val tests = Tests {

    test("transform - nothing") {
      val entry = OutputOpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderField.Sample_Name -> "Sample Name 1"
        )
      )
      assert(OpenLabCDS2CompilCsv("").transform(entry,List()) == List())
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
      assert(OpenLabCDS2CompilCsv("X").transform(entry, List("CompoundX")) == List(None))
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
      assert(OpenLabCDS2CompilCsv("X").transform(entry,List("CompoundX","CompoundY","badCompound"))
        == List(Some("5"),Some("8"),None))
      assert(OpenLabCDS2CompilCsv("X").transform(entry,List("CompoundX","badCompound","CompoundY"))
        == List(Some("5"),None,Some("8")))
    }

    test("transform - Report.txt") {

      val openlabs_output = OpenLabCDSParser.parse(getClass.getResource("/OpenLabCDS/Report.txt").getPath)
      val r = OpenLabCDS2CompilCsv("Area").transform(openlabs_output,List("Glyoxylate","Xylose2","CompoundY"))
      assert( r.length == 3 )
      assert( r.contains(None))
    }
  }
}
