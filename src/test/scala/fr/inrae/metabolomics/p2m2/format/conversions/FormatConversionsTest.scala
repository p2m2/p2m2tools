package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.QuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format.{GCMS, GenericP2M2, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, Xcalibur}
import utest.{TestSuite, Tests, test}
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions._

object FormatConversionsTest extends TestSuite {

  def checkPartialBasic(o : GenericP2M2) = {
    assert(o.values.nonEmpty)
    assert(o.values.head.get(GenericP2M2.HeaderField.metabolite).contains("metabolite"))
    assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
    assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("area"))
    assert(!o.values.head.contains(GenericP2M2.HeaderField.height))
    assert(!o.values.head.contains(GenericP2M2.HeaderField.retTime))
    assert(!o.values.head.contains(GenericP2M2.HeaderField.acquisitionDate))
    assert(!o.values.head.contains(GenericP2M2.HeaderField.injectedVolume))
  }

  def checkBasic(o : GenericP2M2) = {
    assert(o.values.nonEmpty)
    assert(o.values.head.get(GenericP2M2.HeaderField.metabolite).contains("metabolite"))
    assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
    assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("area"))
    assert(o.values.head.get(GenericP2M2.HeaderField.height).contains("height"))
    assert(o.values.head.get(GenericP2M2.HeaderField.retTime).contains("retTime"))
    assert(o.values.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("acquisitionDate"))
    assert(o.values.head.get(GenericP2M2.HeaderField.injectedVolume).contains("injectedVolume"))
  }

  val tests: Tests = Tests{
    test("QuantifyCompoundSummaryReportMassLynx empty object to convert") {


      val o : GenericP2M2 =
        QuantifyCompoundSummaryReportMassLynx(
        origin = "none",
        header = Header(None),
        results = Seq(("metabolite", Seq(Map() )) ) )

      assert(o.values.isEmpty)
    }
    test("QuantifyCompoundSummaryReportMassLynx basic partial object to convert") {
      val o : GenericP2M2 =
          QuantifyCompoundSummaryReportMassLynx(
            origin = "none",
            header = Header(None),
            results = Seq(("metabolite", Seq(
              Map(
                QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "sample",
                QuantifyCompoundSummaryReportMassLynx.HeaderField.Area -> "area",
              ))) ) )

      checkPartialBasic(o)
    }

    test("QuantifyCompoundSummaryReportMassLynx basic object to convert") {
      val o : GenericP2M2 =
        QuantifyCompoundSummaryReportMassLynx(
          origin = "none",
          header = Header(None),
          results = Seq(("metabolite", Seq(
            Map(
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "sample",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Area -> "area",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Height -> "height",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.RT -> "retTime",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date` -> "acquisitionDate",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol` -> "injectedVolume"
            ))) ) )

      checkBasic(o)
    }

    test("Xcalibur empty object to convert") {
      val o : GenericP2M2 =
        Xcalibur(
          origin = "none",
          results = Seq(CompoundSheetXcalibur()))

      assert(o.values.isEmpty)
    }
    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        Xcalibur(
          origin = "none",
          results = Seq(CompoundSheetXcalibur(
            compoundInformationHeader = Map(
              Xcalibur.HeaderSheetField.`Component Name` -> "metabolite"
            ),
            compoundByInjection = Seq(Map(
              Xcalibur.HeaderField.`Sample Name` -> "sample",
              Xcalibur.HeaderField.Area -> "area",
            )))))

      checkPartialBasic(o)
    }
    test("Xcalibur basic object to convert") {
      val o : GenericP2M2 =
        Xcalibur(
          origin = "none",
          results = Seq(CompoundSheetXcalibur(
            compoundInformationHeader = Map(
              Xcalibur.HeaderSheetField.`Component Name` -> "metabolite"
            ),
            compoundByInjection = Seq(Map(
              Xcalibur.HeaderField.`Sample Name` -> "sample",
              Xcalibur.HeaderField.Area -> "area",
              Xcalibur.HeaderField.Height -> "height",
              Xcalibur.HeaderField.RT -> "retTime",
              Xcalibur.HeaderField.`Acq Date` -> "acquisitionDate",
              Xcalibur.HeaderField.`Inj Vol` -> "injectedVolume"
            )))))

      checkBasic(o)
    }
    test("GCMS empty object to convert") {
      val o : GenericP2M2 =
        GCMS(origin = "none")

      assert(o.values.isEmpty)
    }

    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        GCMS(
          origin = "none",
          header=Map(),
          ms_quantitative_results = Seq(Map(
            GCMS.HeaderField.Name -> "sample",
            GCMS.HeaderField.Area -> "area"))
        )

      assert(o.values.nonEmpty)
      assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
      assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("area"))
    }
    test("OpenlabCDS empty object to convert") {
      val o : GenericP2M2 =
        OpenLabCDS(origin = "none")

      assert(o.values.isEmpty)
    }

    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        OpenLabCDS(
          origin = "none",
          header=Map(
            OpenLabCDS.HeaderFileField.`Sample Name` -> "sample",
            OpenLabCDS.HeaderFileField.`Inj Volume` -> "0.1",
            OpenLabCDS.HeaderFileField.`Last changed Acq. Method` -> "12/12/2022"
          )
          ,
          results = Seq(Map(
            OpenLabCDS.HeaderField.RetTime -> "0.1",
            OpenLabCDS.HeaderField.Name -> "metabolite",
            OpenLabCDS.HeaderField.Area -> "area",
            OpenLabCDS.HeaderField.Amount -> "10",
          ))
        )

      assert(o.values.nonEmpty)
      assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
      assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("area"))
    }
  }
}
