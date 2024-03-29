package fr.inrae.metabolomics.p2m2.format

import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, Isocor, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, QuantifySummaryReportMassLynx, Xcalibur}
import fr.inrae.metabolomics.p2m2.format.ms.QuantifySummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.stream.GenericP2M2FormatExtended
import utest.{TestSuite, Tests, test}

object MassSpectrometryResultSetFactoryTest extends TestSuite {
  val tests: Tests = Tests{
    test("GenericP2M2") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(GenericP2M2())).contains(GenericP2M2()))
    }
    test("GCMS") {
      assert(
       MassSpectrometryResultSetFactory.build(
         MassSpectrometryResultSetFactory.stringify(GCMS("origin"))).contains(GCMS("origin")))
    }
    test("OpenLabCDS") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(OpenLabCDS("origin"))).contains(OpenLabCDS("origin"))
      )
    }
    test("QuantifyCompoundSummaryReportMassLynx") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(
            QuantifyCompoundSummaryReportMassLynx("origin",Header(None))))
          .contains(QuantifyCompoundSummaryReportMassLynx("origin",Header(None)))
      )
    }

    test("QuantifySampleSummaryReportMassLynx") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(
            QuantifySampleSummaryReportMassLynx("origin", Header(None))))
          .contains(QuantifySampleSummaryReportMassLynx("origin", Header(None)))
      )
    }

    test("Xcalibur") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(Xcalibur("origin"))).contains(Xcalibur("origin")))
    }
    test("Isocor") {
      assert(
        MassSpectrometryResultSetFactory.build(
          MassSpectrometryResultSetFactory.stringify(Isocor("origin"))).contains(Isocor("origin")))
    }

    test("None") {
      assert(MassSpectrometryResultSetFactory.build("something").isEmpty)
      assert(MassSpectrometryResultSetFactory.stringify(null).isEmpty)
    }
  }
}
