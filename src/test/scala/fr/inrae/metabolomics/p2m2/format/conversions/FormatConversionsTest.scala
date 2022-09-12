package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.ms.QuantifySummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur.CompoundSheetXcalibur
import utest.{TestSuite, Tests, test}
import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions._
import fr.inrae.metabolomics.p2m2.format.ms.{GCMS, GenericP2M2, Isocor, OpenLabCDS, QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx, Xcalibur}

object FormatConversionsTest extends TestSuite {

  def checkPartialBasic(o : GenericP2M2): Unit = {
    assert(o.samples.nonEmpty)
    assert(o.samples.head.get(GenericP2M2.HeaderField.metabolite).contains("metabolite"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
    assert(!o.samples.head.contains(GenericP2M2.HeaderField.area))
    assert(!o.samples.head.contains(GenericP2M2.HeaderField.height))
    assert(!o.samples.head.contains(GenericP2M2.HeaderField.retTime))
 //   assert(!o.values.head.contains(GenericP2M2.HeaderField.acquisitionDate))
    assert(!o.samples.head.contains(GenericP2M2.HeaderField.injectedVolume))
  }

  def checkBasic(o : GenericP2M2): Unit = {
    assert(o.samples.nonEmpty)
    assert(o.samples.head.get(GenericP2M2.HeaderField.metabolite).contains("metabolite"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.area).contains("1.0"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.height).contains("2.0"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.retTime).contains("3.0"))
   // assert(o.values.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("acquisitionDate"))
    assert(o.samples.head.get(GenericP2M2.HeaderField.injectedVolume).contains("injectedVolume"))
  }

  val tests: Tests = Tests{
    test("QuantifyCompoundSummaryReportMassLynx empty object to convert") {
      val o : GenericP2M2 =
        QuantifyCompoundSummaryReportMassLynx(
        origin = "none",
        header = Header(None),
        resultsByCompound = Seq(("metabolite", Seq(Map() )) ) )

      assert(o.samples.isEmpty)
    }
    test("QuantifyCompoundSummaryReportMassLynx basic partial object to convert") {
      val o : GenericP2M2 =
          QuantifyCompoundSummaryReportMassLynx(
            origin = "none",
            header = Header(None),
            resultsByCompound = Seq(("metabolite", Seq(
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
          header = Header(Some("Fri Sep 20 14:23:33 2019")),
          resultsByCompound = Seq(("metabolite", Seq(
            Map(
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "sample",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Area -> "1.0",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Height -> "2.0",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.RT -> "3.0",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.`Acq.Date` -> "17-sept-19",
              QuantifyCompoundSummaryReportMassLynx.HeaderField.`Inj. Vol` -> "injectedVolume"
            ))) ) )
      assert(o.samples.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("2019-09-17 00:00:00.0000"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.exportDate).contains("2019-09-20 14:23:33.0000"))
      checkBasic(o)
    }

    test("QuantifySampleSummaryReportMassLynxToGenericP2M2 basic object to convert") {
      val o: GenericP2M2 =
        QuantifySampleSummaryReportMassLynx(
          origin = "none",
          header = Header(Some("Fri Sep 20 14:23:33 2019")),
          resultsBySample = Seq(("sample", Seq(
            Map(
              QuantifySampleSummaryReportMassLynx.HeaderField.Name -> "metabolite",
              QuantifySampleSummaryReportMassLynx.HeaderField.Area -> "1.0",
              QuantifySampleSummaryReportMassLynx.HeaderField.Height -> "2.0",
              QuantifySampleSummaryReportMassLynx.HeaderField.RT -> "3.0",
              QuantifySampleSummaryReportMassLynx.HeaderField.`Acq.Date` -> "17-sept-19"
            )))))

      assert(o.samples.nonEmpty)
      assert(o.samples.head.get(GenericP2M2.HeaderField.metabolite).contains("metabolite"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.area).contains("1.0"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.height).contains("2.0"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.retTime).contains("3.0"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("2019-09-17 00:00:00.0000"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.exportDate).contains("2019-09-20 14:23:33.0000"))

    }

    test("QSSRMassLynxToQCSRMassLynx basic object to convert") {
      val x = QuantifySampleSummaryReportMassLynx(
        origin = "none",
        header = Header(Some("Fri Sep 20 14:23:33 2019")),
        resultsBySample = Seq(("sample", Seq(
          Map(
            QuantifySampleSummaryReportMassLynx.HeaderField.Name -> "metabolite",
            QuantifySampleSummaryReportMassLynx.HeaderField.Area -> "1.0",
            QuantifySampleSummaryReportMassLynx.HeaderField.Height -> "2.0",
            QuantifySampleSummaryReportMassLynx.HeaderField.RT -> "3.0",
            QuantifySampleSummaryReportMassLynx.HeaderField.`Acq.Date` -> "17-sept-19"
          )))))

      val y = QSSRMassLynxToQCSRMassLynx(x)
      val o1 : GenericP2M2 = x
      val o2 : GenericP2M2= y
      assert(o1==o2)
    }

    test("Xcalibur empty object to convert") {
      val o : GenericP2M2 =
        Xcalibur(
          origin = "none",
          results = Seq(CompoundSheetXcalibur()))

      assert(o.samples.isEmpty)
    }
    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        Xcalibur(
          origin = "none",
          results = Seq(CompoundSheetXcalibur(
            compoundInformationHeader = Map(
              Xcalibur.HeaderSheetField.`Component Name` -> "metabolite",
              Xcalibur.HeaderSheetField.Date -> "07/06/2022 08:57:12",
            ),
            compoundByInjection = Seq(Map(
              Xcalibur.HeaderField.Filename -> "sample",
              Xcalibur.HeaderField.Area -> "area",
            )))))

      assert(o.samples.head.get(GenericP2M2.HeaderField.exportDate).contains("2022-06-07 08:57:12.0000"))
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
              Xcalibur.HeaderField.Filename -> "sample",
              Xcalibur.HeaderField.Area -> "1.0",
              Xcalibur.HeaderField.Height -> "2.0",
              Xcalibur.HeaderField.RT -> "3.0",
              Xcalibur.HeaderField.`Acq Date` -> "Tue Jun 20 14:53:08 CEST 2017",
              Xcalibur.HeaderField.`Inj Vol` -> "injectedVolume"
            )))))

      checkBasic(o)
      assert(o.samples.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("2017-06-20 14:53:08.0000"))
    }

    test("GCMS empty object to convert") {
      val o : GenericP2M2 =
        GCMS(origin = "none")

      assert(o.samples.isEmpty)
    }

    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        GCMS(
          origin = "none",
          header=Map(),
          msQuantitativeResults = Seq(Map(
            GCMS.HeaderField.Name -> "sample",
            GCMS.HeaderField.Area -> "area"
          ))
        )

      assert(o.samples.nonEmpty)
      assert(o.samples.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
      assert(!o.samples.head.contains(GenericP2M2.HeaderField.area))
    }
    test("OpenlabCDS empty object to convert") {
      val o : GenericP2M2 =
        OpenLabCDS(origin = "none")

      assert(o.samples.isEmpty)
    }

    test("Xcalibur basic partial object to convert") {
      val o : GenericP2M2 =
        OpenLabCDS(
          origin = "none",
          header=Map(
            OpenLabCDS.HeaderFileField.`Sample Name` -> "sample",
            OpenLabCDS.HeaderFileField.`Inj Volume` -> "0.1",
            OpenLabCDS.HeaderFileField.`Last changed Acq. Method` -> "2/25/2021 3:02:59 PM",
            OpenLabCDS.HeaderFileField.`Last changed Analysis Method` -> "2/26/2021 3:03:00 PM"
          )
          ,
          results = Seq(Map(
            OpenLabCDS.HeaderField.RetTime -> "0.1",
            OpenLabCDS.HeaderField.Name -> "metabolite",
            OpenLabCDS.HeaderField.Area -> "area",
            OpenLabCDS.HeaderField.Amount -> "10",
          ))
        )

      assert(o.samples.nonEmpty)
      assert(o.samples.head.get(GenericP2M2.HeaderField.sample).contains("sample"))
      assert(!o.samples.head.contains(GenericP2M2.HeaderField.area))
      assert(o.samples.head.get(GenericP2M2.HeaderField.acquisitionDate).contains("2021-02-25 15:02:59.0000"))
      assert(o.samples.head.get(GenericP2M2.HeaderField.exportDate).contains("2021-02-26 15:03:00.0000"))
    }

    test("formatDateWithLocalDate") {
      assert(FormatConversions.formatDateWithLocalDate(Some("13-May-22"),FormatConversions.formatMassLynxTxt).isDefined)
      assert(FormatConversions.formatDateWithLocalDate(Some("ERROR"),FormatConversions.formatMassLynxTxt).contains("ERROR"))
      assert(FormatConversions.formatDateWithLocalDate(Some("13-May-22"),FormatConversions.formatMassLynxTxt).contains("2022-05-13 00:00:00.0000"))
      assert(FormatConversions.formatDateWithLocalDate(Some("23/08/2021"),FormatConversions.formatGCMS).isDefined)
      assert(FormatConversions.formatDateWithLocalDate(Some("23/08/2021"),FormatConversions.formatGCMS).contains("2021-08-23 00:00:00.0000"))
    }
    test("formatDateWithLocalDate") {
      assert(FormatConversions.formatDateWithLocalDateTime(Some("2/25/2021 3:02:59 PM"),FormatConversions.formatOpenLabCDS).isDefined)
      assert(FormatConversions.formatDateWithLocalDateTime(Some("ERROR"),FormatConversions.formatMassLynxTxt).contains("ERROR"))
      assert(FormatConversions.formatDateWithLocalDateTime(Some("2/25/2021 3:02:59 PM"),FormatConversions.formatOpenLabCDS).contains("2021-02-25 15:02:59.0000"))
    }
    test("Isocor toGenericP2M2") {
      val g : GenericP2M2 = Isocor(origin="").toGenericP2M2
      assert(g.samples==Seq())
    }
    test("GenericP2M2 toGenericP2M2") {
      val g : GenericP2M2 = GenericP2M2().toGenericP2M2
      assert(g.samples==Seq())
    }
  }
}
