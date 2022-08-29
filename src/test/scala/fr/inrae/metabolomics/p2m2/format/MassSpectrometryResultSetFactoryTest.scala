package fr.inrae.metabolomics.p2m2.format
import fr.inrae.metabolomics.p2m2.format.QuantifyCompoundSummaryReportMassLynx.Header
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
  }
}
