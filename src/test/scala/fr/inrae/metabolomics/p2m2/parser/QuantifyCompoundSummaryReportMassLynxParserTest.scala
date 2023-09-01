package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.{QuantifyCompoundSummaryReportMassLynx, QuantifySampleSummaryReportMassLynx}
import fr.inrae.metabolomics.p2m2.format.ms.QuantifyCompoundSummaryReportMassLynx.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.QuantifyCompoundSummaryReportMassLynx.HeaderField.HeaderField
import fr.inrae.metabolomics.p2m2.format.ms.QuantifySummaryReportMassLynx.Header
import utest.{TestSuite, Tests, test}

import scala.util.{Failure, Success, Try}

object QuantifyCompoundSummaryReportMassLynxParserTest extends TestSuite {

  val injectionTest1 : Map[HeaderField,String]=Seq(
    HeaderField.`Num. Injection` -> 1,
    HeaderField.Name -> "GlyN15_A_3",
    HeaderField.Trace ->188,
    HeaderField.Type ->"",
    HeaderField.`Std. Conc` ->"",
    HeaderField.RT ->1.78,
    HeaderField.Area ->96688,
    HeaderField.`%Dev` ->"",
    HeaderField.`S/N` ->796,
    HeaderField.`Vial` ->"1:A,6",
    HeaderField.`Height/Area` ->11.911,
    HeaderField.`Acq.Date` ->"17-sept-19",
    HeaderField.Height ->1151660).map(v => v._1 -> v._2.toString).filter(_._2!="").toMap

  val injectionTest2 : Map[HeaderField,String]=Seq(
    HeaderField.`Num. Injection` -> 1,
    HeaderField.Name -> "SE1_GlyN15_3",
    HeaderField.Trace ->189,
    HeaderField.Type ->"",
    HeaderField.`Std. Conc` ->"",
    HeaderField.RT ->1.75,
    HeaderField.Area ->16945,
    HeaderField.`%Dev` ->"",
    HeaderField.`S/N` ->58,
    HeaderField.`Vial` ->"1:A,3",
    HeaderField.`Height/Area` ->12.562,
    HeaderField.`Acq.Date` ->"17-sept-19",
    HeaderField.Height ->212863).map(v => v._1 -> v._2.toString).filter(_._2!="").toMap

  val tests: Tests = Tests{

  test("file empty") {
    val toParse = ""
    assert( QuantifySummaryReportMassLynxParser.parseHeader(toParse.split("\n").toList) == Header() )
  }

  test("empty file - no date, no compound") {
    val toParse =
      """Quantify Compound Summary Report """.stripMargin
    assert( QuantifySummaryReportMassLynxParser.parseHeader(toParse.split("\n").toList) == Header() )
  }

  test("empty file - no compound") {
    val toParse =
      """Quantify Compound Summary Report
        |
        |Printed Fri Sep 20 14:23:33 2019
        |
        |
        |""".stripMargin

    val h = Header(Some("Fri Sep 20 14:23:33 2019"))

    assert( QuantifySummaryReportMassLynxParser.parseHeader(toParse.split("\n").toList) == h )
  }

  test("parse empty compound") {
    val toParse =
      """Compound 1:  NH4+""".stripMargin
    assert(QuantifySummaryReportMassLynxParser
      .parseResultsByElement(
        QuantifyCompoundSummaryReportMassLynx.HeaderField,toParse.split("\n").toList) == List(("NH4+",List())))
  }

  test("parse compound") {
    val toParse =
      """Compound 1:  NH4+
        |
        |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
        |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin
    assert(QuantifySummaryReportMassLynxParser
      .parseResultsByElement(
        QuantifyCompoundSummaryReportMassLynx.HeaderField,
        toParse.split("\n").toList) == List(("NH4+", List(injectionTest1))))
  }

  test("Test Unknown Name") {
    val toParse =
      """Compound 1:  TATA
        |
        |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
        |1		188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

    assert(!QuantifySummaryReportMassLynxParser
      .parseResultsByElement(
        QuantifyCompoundSummaryReportMassLynx.HeaderField,
        toParse.split("\n").toList).head._2.head.contains(HeaderField.Name))
  }

  test("full file content") {
    val toParse = """Quantify Compound Summary Report
                    |
                    |Printed Fri Sep 20 14:23:33 2019
                    |
                    |Compound 1:  NH4+
                    |
                    |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
                    |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660
                    |
                    |Compound 2:  NH4+, M+1
                    |
                    |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
                    |1	SE1_GlyN15_3	189			1.75	16945			58	1:A,3	12.562	17-sept-19	212863
                    |""".stripMargin

    val results : QuantifyCompoundSummaryReportMassLynx = QuantifySummaryReportMassLynxParser.getCompoundSummaryReport("test",toParse.split("\n").toList)

    assert( results.header == Header(Some("Fri Sep 20 14:23:33 2019")) )
    assert( results.resultsByCompound == List(("NH4+", List(injectionTest1)),("NH4+, M+1", List(injectionTest2))))
  }

  test("complete file") {
    val results : QuantifyCompoundSummaryReportMassLynx =
      QuantifySummaryReportMassLynxParser.parseFile(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath)
        .toQuantifyCompoundSummaryReportMassLynx

    assert( results.header == Header(Some("Fri Sep 20 14:23:33 2019")) )
    assert( results.resultsByCompound.length == 163  )
  }

  test("targeted compound file") {
    val results : QuantifyCompoundSummaryReportMassLynx =
      QuantifySummaryReportMassLynxParser
        .parseFile(
          getClass.getResource("/MassLynx/targeted/190522_97.txt").getPath)
        .toQuantifyCompoundSummaryReportMassLynx

    assert( results.header == Header(Some("Thu Jun 09 13:49:54 2022")) )
    assert( results.resultsByCompound.length == 6  )
    assert(results.resultsByCompound.map(_._2).exists(_.nonEmpty))
  }

  test("extensionIsCompatible") {
    assert(QuantifySummaryReportMassLynxParser.extensionIsCompatible("/GCMS/13CPROT4.txt"))
    assert(!QuantifySummaryReportMassLynxParser.extensionIsCompatible(""))
    assert(QuantifySummaryReportMassLynxParser.extensionIsCompatible("/GCMS/13CPROT4.xls"))
  }

  test("sniffFile") {
    assert(!QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/GCMS/13CPROT4.txt").getPath))
    assert(QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath))
    assert(!QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath))
    assert(!QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
    assert(!QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/Xcalibur/bad_file_xls.xls").getPath))
    assert(!QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/MassLynx/xml/quandata.xml").getPath))
  }

    test("fix Mod.Time MassLynx") {
      assert(QuantifySummaryReportMassLynxParser.sniffFile(getClass.getResource("/MassLynx/targeted/070120_Saturation_CQA.txt").getPath))
    }

    test("SortieMassLynxToutesColonnes.txt") {
      val o : Boolean =
        QuantifySummaryReportMassLynxParser.sniffFile(
          getClass.getResource("/MassLynx/targeted/SortieMassLynxSampleToutesColonnes.txt").getPath)
      assert(o)
    }

    test("conversion QuantifyCompoundSummaryReportMassLynx -> QuantifySampleSummaryReportMassLynx") {
      val results: QuantifyCompoundSummaryReportMassLynx =
        QuantifySummaryReportMassLynxParser.parseFile(getClass.getResource("/MassLynx/targeted/070120_Saturation_CQA.txt").getPath)
          .toQuantifyCompoundSummaryReportMassLynx

      val resOfOneSample = results.toQuantifySampleSummaryReportMassLynx
        .resultsBySample
        .filter( _._1 == """070120_CQA_22,18µgmL""")
        .map(_._2)

      assert(resOfOneSample.length == 4)
    }

    test("conversion SortieMassLynxCompoundToutesColonnes.txt") {
      assert(Try(QuantifySummaryReportMassLynxParser.parseFile(
        getClass.getResource("/MassLynx/targeted/SortieMassLynxCompoundToutesColonnes.txt").getPath)).isSuccess)
    }

    test("conversion SortieMassLynxSampleToutesColonnes.txt") {
    val o = QuantifySummaryReportMassLynxParser.parseFile(
      getClass.getResource("/MassLynx/targeted/SortieMassLynxSampleToutesColonnes.txt").getPath)

      assert(o.toQuantifySampleSummaryReportMassLynx.resultsBySample.nonEmpty)

    }

  }


}
