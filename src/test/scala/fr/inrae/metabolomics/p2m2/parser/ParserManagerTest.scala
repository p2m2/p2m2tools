package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, Xcalibur}
import utest.{TestSuite, Tests, test}

import java.io.{BufferedReader, FileInputStream}
import java.nio.file.{Files, Paths}

object ParserManagerTest extends TestSuite {

  val tests: Tests = Tests {
    test("file empty") {
      assert(ParserManager.buildMassSpectrometryObject("").isEmpty)
    }
    test("13CPROT4.txt isDefined") {
      assert(ParserManager.buildMassSpectrometryObject(getClass.getResource("/GCMS/13CPROT4.txt").getPath).isDefined)
    }
    test("13CPROT4 isInstanceOf GCMS") {
      ParserManager.buildMassSpectrometryObject(getClass.getResource("/GCMS/13CPROT4.txt").getPath).foreach {
        x => assert(x.isInstanceOf[GCMS])
      }
    }

    test("Report_Ex1 isDefined") {
      assert(ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath).isDefined)
    }
    test("Report_Ex1 isInstanceOf OpenLabCDS") {
      ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath).foreach {
        x => assert(x.isInstanceOf[OpenLabCDS])
      }
      }
    test("mass_15Ngly isDefined") {
      assert(ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath).isDefined)
    }
    test("mass_15Ngly isInstanceOf QuantifyCompoundSummaryReportMassLynx") {
      ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath).foreach {
        x => assert(x.isInstanceOf[QuantifyCompoundSummaryReportMassLynx])
      }
    }
    test("resuts_inj1_Long isDefined") {
      assert(ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath).isDefined)
    }
    test("resuts_inj1_Long isInstanceOf Xcalibur") {
      ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath).foreach {
        x => assert(x.isInstanceOf[Xcalibur])
      }
    }

    test("empty byte array") {
      assert(ParserManager.buildMassSpectrometryObject(Array[Byte]()).isEmpty)
    }
    test("13CPROT4.txt / byte array isDefined") {
      val path = Files.readAllBytes(Paths.get(getClass.getResource("/GCMS/13CPROT4.txt").getPath))
      assert(ParserManager.buildMassSpectrometryObject(path).isDefined)
    }
    test("Report_Ex1  / byte array isDefined") {
      val path = Files.readAllBytes(Paths.get(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath))
      assert(ParserManager.buildMassSpectrometryObject(path).isDefined)
    }
    test("mass_15Ngly  / byte array isDefined") {
      val path = Files.readAllBytes(Paths.get(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath))
      assert(ParserManager.buildMassSpectrometryObject(path).isDefined)
    }
    test("resuts_inj1_Long  / byte array isDefined") {
      val path = Files.readAllBytes(Paths.get(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
      val o1 = ParserManager.buildMassSpectrometryObject(path)
      assert(o1.isDefined)
      val o2 = ParserManager.buildMassSpectrometryObject(
        getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      assert(o1.get.toGenericP2M2 == o2.get.toGenericP2M2)
    }
  }
}
