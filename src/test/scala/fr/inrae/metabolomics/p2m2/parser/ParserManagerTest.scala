package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.{GCMS, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, Xcalibur}
import utest.{TestSuite, Tests, test}

object ParserManagerTest extends TestSuite {

  val tests: Tests = Tests {
    test("file empty") {
      assert(ParserManager.buildMassSpectrometryObject("").isEmpty)
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/GCMS/13CPROT4.txt").getPath).isDefined)
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/GCMS/13CPROT4.txt").getPath).get.isInstanceOf[GCMS])

      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath).isDefined)
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath).get.isInstanceOf[OpenLabCDS])

      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath).isDefined)
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath).get.isInstanceOf[QuantifyCompoundSummaryReportMassLynx])

      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath).isDefined)
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath).get.isInstanceOf[Xcalibur])

    }
  }
}
