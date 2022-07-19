package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.QuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.format.{GenericP2M2, QuantifyCompoundSummaryReportMassLynx}
import utest.{TestSuite, Tests, test}

object QuantifyCompoundSummaryReportMassLynxToGenericP2M2Test extends TestSuite {
  val tests: Tests = Tests{
    test("empty object to convert") {
      val o : GenericP2M2 =
        QuantifyCompoundSummaryReportMassLynxToGenericP2M2.QuantifyCompoundSummaryReportMassLynxToGenericP2M2(
        QuantifyCompoundSummaryReportMassLynx(
        origin = "none",
        header = Header(None),
        results = Seq(("testCompound", Seq(Map() )) ) ))

      assert(o.values.isEmpty)
    }
    test("basic partial object to convert") {
      val o : GenericP2M2 =
        QuantifyCompoundSummaryReportMassLynxToGenericP2M2.QuantifyCompoundSummaryReportMassLynxToGenericP2M2(
          QuantifyCompoundSummaryReportMassLynx(
            origin = "none",
            header = Header(None),
            results = Seq(("testCompound", Seq(
              Map(
                QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "Name",
                QuantifyCompoundSummaryReportMassLynx.HeaderField.Area -> "10000",
              ))) ) ))

      assert(o.values.nonEmpty)
      assert(o.values.head.get(GenericP2M2.HeaderField.metabolite).contains("testCompound"))
      assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("Name"))
      assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("10000"))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.height))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.retTime))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.acquisitionDate))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.injectedVolume))
    }
  }
}
