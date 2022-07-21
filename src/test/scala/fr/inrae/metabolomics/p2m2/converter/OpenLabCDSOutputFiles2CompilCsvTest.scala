package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.format.OpenLabCDS
import fr.inrae.metabolomics.p2m2.parser.OpenLabCDSParser
import OpenLabCDS.{HeaderField, HeaderFileField}
import utest.{TestSuite, Tests, test}

object OpenLabCDSOutputFiles2CompilCsvTest extends TestSuite {
  val tests: Tests = Tests {

    test("transform - nothing") {
      val entry = OpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.`Sample Name` -> "Sample Name 1"
        )
      )
      assert(OpenLabCDS2CompilCsv("").transform(entry,List()) == List())
    }

    test("transform - nothing") {
      val entry = OpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.`Sample Name` -> "Sample Name 1",
        ),
        results = List(Map())
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert(OpenLabCDS2CompilCsv("X").transform(entry, List("CompoundX")) == List(None))
    }

    test("transform - ok 1") {
      val entry = OpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.`Sample Name` -> "Sample Name 1",
        ),
        results = List(
          Map(
            HeaderField.Name -> "CompoundX",
            HeaderField.RetTime -> "5",
            HeaderField.Area -> "10"
          ),
          Map(
            HeaderField.Name -> "CompoundY",
            HeaderField.RetTime -> "8"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert(OpenLabCDS2CompilCsv("RetTime").transform(entry,List("CompoundX","CompoundY","badCompound"))
        == List(Some("5"),Some("8"),None))
      assert(OpenLabCDS2CompilCsv("RetTime").transform(entry,List("CompoundX","badCompound","CompoundY"))
        == List(Some("5"),None,Some("8")))
      assert(OpenLabCDS2CompilCsv("Area").transform(entry,List("CompoundX","CompoundY","badCompound"))
        == List(Some("10"),None,None))
      assert(OpenLabCDS2CompilCsv("NoHeaderColumnExist").transform(entry,List("CompoundX","CompoundY","badCompound"))
        == List(None,None,None))
    }

    test("transform - uname compound") {
      val entry = OpenLabCDS(
        origin = "file/SAMPLE",
        header = Map(
          HeaderFileField.`Sample Name` -> "Sample Name 1",
        ),
        results = List(
          Map(
            HeaderField.Name -> "",
            HeaderField.Area -> "10"
          )
        )
      )
      /* Name should be [Metabolite]_[Derivative]_[Isotopologue] */
      assert(OpenLabCDS2CompilCsv("RetTime").transform(entry,List("CompoundX"))
        == List(None))
    }

    test("transform - Report.txt") {

      val openLabsOutput = OpenLabCDSParser.parse(getClass.getResource("/OpenLabCDS/Report.txt").getPath)
      val r = OpenLabCDS2CompilCsv("Area").transform(openLabsOutput,List("Glyoxylate","Xylose2","CompoundY"))
      assert( r.length == 3 )
      assert( r.contains(None))
    }
  }
}
