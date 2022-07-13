package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.QuantitativeDataProcessingMassLynx.{CalibrationData, SecondaryPeaks}
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
  }
}
