package fr.inrae.metabolomics.p2m2.format

import fr.inrae.metabolomics.p2m2.format.Isocor.CompoundIsocor
import fr.inrae.metabolomics.p2m2.format.QuantifyCompoundSummaryReportMassLynx.Header
import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import utest.{TestSuite, Tests, test}
import upickle.default._

import scala.util.Try

object SerializationTest extends TestSuite {
  val tests: Tests = Tests{
    test("GenericP2M2 empty") {
      assert(Try(read[GenericP2M2](write(GenericP2M2()))).isSuccess)
    }

    test("GCMS empty") {
      assert(Try(read[GCMS](write(GCMS("origin")))).isSuccess)
    }

    test("OpenLabCDS empty") {
      assert(Try(read[OpenLabCDS](write(OpenLabCDS("origin")))).isSuccess)
    }

    test("QuantifyCompoundSummaryReportMassLynx empty") {
      assert(Try(read[QuantifyCompoundSummaryReportMassLynx](
        write(QuantifyCompoundSummaryReportMassLynx("origin",Header(None))))).isSuccess)
    }

    test("Xcalibur empty") {
      assert(Try(read[Xcalibur](
        write(Xcalibur("origin")))).isSuccess)
    }

    test("Isocor") {
      assert(Try(read[Isocor](
        write(Isocor("origin")))).isSuccess)
    }

    test("GenericP2M2") {
      val v = GenericP2M2(Seq( Map(
        GenericP2M2.HeaderField.sample -> "sample",
        GenericP2M2.HeaderField.height -> "h",
        GenericP2M2.HeaderField.retTime -> "0.5",
        GenericP2M2.HeaderField.metabolite -> "metabolite",
      )))
      assert(Try(read[GenericP2M2](write(v))).isSuccess)
      assert(read[GenericP2M2](write(v)) == v)
    }

    test("GCMS") {
      val v = GCMS(
        origin = "origin",
        header = Map(
          GCMS.HeaderFileField.Data_File_Name -> "info1",
          GCMS.HeaderFileField.Output_Date -> "info2",
          GCMS.HeaderFileField.Output_Time -> "info3",
        ),
        msQuantitativeResults = Seq(
          Map(
            GCMS.HeaderField.`ID#` -> "1",
            GCMS.HeaderField.Name -> "sample",
            GCMS.HeaderField.`Peak#`-> "p1",
          ),
          Map(
            GCMS.HeaderField.`ID#` -> "2",
            GCMS.HeaderField.Name -> "sample",
            GCMS.HeaderField.`Peak#` -> "p1",
            GCMS.HeaderField.`Ref.Ion Height` -> "h1",
          ),
        )
      )
      assert(Try(read[GCMS](write(v))).isSuccess)
      assert(read[GCMS](write(v)) == v)
    }

    test("OpenLabCDS") {
      val v = OpenLabCDS(
        origin = "origin",
        header = Map(
          OpenLabCDS.HeaderFileField.`Sample Name` -> "sample",
          OpenLabCDS.HeaderFileField.`Acq. Operator` -> "h",
          OpenLabCDS.HeaderFileField.`Sample Operator` -> "0.5",
          OpenLabCDS.HeaderFileField.`Additional Info` -> "metabolite",
        ),
        results = Seq(
          Map(
            OpenLabCDS.HeaderField.RetTime -> "sample",
            OpenLabCDS.HeaderField.ISTD->"istd"
          ))
      )
      assert(Try(read[OpenLabCDS](write(v))).isSuccess)
      assert(read[OpenLabCDS](write(v)) == v)
    }

    test("QuantifyCompoundSummaryReportMassLynx") {
      val v = QuantifyCompoundSummaryReportMassLynx(
        origin = "origin",
        header = Header(None),
        results = Seq(
          ("TA" , Seq(
            Map(
            QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "nameTest"
            ),
            Map(
              QuantifyCompoundSummaryReportMassLynx.HeaderField.Name -> "nameTest2"
            )
          )
          )
        )
      )
      assert(Try(read[QuantifyCompoundSummaryReportMassLynx](write(v))).isSuccess)
      assert(read[QuantifyCompoundSummaryReportMassLynx](write(v)) == v)
    }

    test("Xcalibur") {
      val v = Xcalibur(
        origin = "origin",
        results = Seq(
          CompoundSheetXcalibur(
            compoundInformationHeader = Map(
              Xcalibur.HeaderSheetField.`Full Name` -> "Full name",
              Xcalibur.HeaderSheetField.`User Name` -> "User name",
            ),
            compoundByInjection = Seq(
              Map(
                Xcalibur.HeaderField.RT -> "0.5"
              ),
              Map(
                Xcalibur.HeaderField.RT -> "0.6"
              ),
            )
          )
        )
      )
      assert(Try(read[Xcalibur](write(v))).isSuccess)
      assert(read[Xcalibur](write(v)) == v)
    }

    test("Isocor") {
      val v = Isocor(
        origin = "origin",
        results = Seq(
          CompoundIsocor(Map(
            Isocor.HeaderField.sample -> "s1"
          )),
          CompoundIsocor(Map(
            Isocor.HeaderField.sample -> "s2"
          ))
          )
        )
      assert(Try(read[Isocor](write(v))).isSuccess)
      assert(read[Isocor](write(v)) == v)
    }
  }
}