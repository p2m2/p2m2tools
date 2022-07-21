package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.XMLQuantitativeDataProcessingMassLynx.{CalibrationData, SecondaryPeaks}
import utest.{TestSuite, Tests, test}

import scala.xml.XML

object QuantitativeDataProcessingMassLynxParserTest extends TestSuite{
  val tests: Tests = Tests {
    test("file empty") {
      QuantitativeDataProcessingMassLynxParser.parse(getClass.getResource("/MassLynx/xml/quandata.xml").getPath)
    }

    test("calibration") {
      val txt =
        """
          |<CALIBRATIONDATA filename="test" modifieddate="test" modifiedtime="test"/>
          |""".stripMargin
      val r = CalibrationData.fromXml(XML.loadString(txt))
      assert(r.filename=="test")
      assert(r.modifiedDate=="test")
      assert(r.modifiedTime=="test")
    }

    test("SecondaryPeaks") {
      val txt =
        """
          |<SECONDARYPEAKS area="test" heigth="test" irwinflag="test" actualionratio="test" secondarychromnoise="test"/>
          |""".stripMargin
      val r = SecondaryPeaks.fromXml(XML.loadString(txt))
      assert(r.area=="test")
      assert(r.heigth=="test")
      assert(r.irwinflag=="test")
      assert(r.actualIonRatio=="test")
      assert(r.secondarychromnoise=="test")
    }

    test("extensionIsCompatible") {
      assert(!QuantitativeDataProcessingMassLynxParser.extensionIsCompatible("/GCMS/13CPROT4.txt"))
      assert(!QuantitativeDataProcessingMassLynxParser.extensionIsCompatible(""))
      assert(!QuantitativeDataProcessingMassLynxParser.extensionIsCompatible("/GCMS/13CPROT4.xls"))
      assert(QuantitativeDataProcessingMassLynxParser.extensionIsCompatible("/MassLynx/xml/quandata.xml"))
    }

    test("sniffFile") {
      assert(!QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/GCMS/13CPROT4.txt").getPath))
      assert(!QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath))
      assert(!QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath))
      assert(!QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
      assert(!QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/Xcalibur/bad_file_xls.xls").getPath))
      assert(QuantitativeDataProcessingMassLynxParser.sniffFile(getClass.getResource("/MassLynx/xml/quandata.xml").getPath))
    }
  }
}
