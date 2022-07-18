package fr.inrae.metabolomics.p2m2.command

import fr.inrae.metabolomics.p2m2.command.MassLynx2IsocorCommand.build_results_element
import fr.inrae.metabolomics.p2m2.converter.MassLynxOutput2IsocorInput
import fr.inrae.metabolomics.p2m2.parser.QuantifyCompoundSummaryReportMassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.{Isocor, QuantifyCompoundSummaryReportMassLynx}
import utest.{TestSuite, Tests, test}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

object MassLynx2IsocorCommandTest extends TestSuite {

  val tests: Tests = Tests {

    test("MassLynx2IsocorCommand - help") {
      Try(MassLynx2IsocorCommand.main(Array())) match {
        case Success(_) => assert(true)
        case Failure(f) => f.printStackTrace();assert(false)

      }
    }

    test("MassLynx2IsocorCommand - with args") {
      val tp = File.createTempFile("out_13C-", ".tsv").getPath
      val tp2 = File.createTempFile("out_15N-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,
          "--out_13C",tp,
          "--out_15N",tp2
        ))) match {
        case Success(_) => assert(true)
        case Failure(f) => f.printStackTrace();assert(false)
      }
    }

    test("MassLynx2IsocorCommand - with args derivatives (empty file)") {
      val tp = File.createTempFile("out_13C-", ".tsv").getPath
      val tp2 = File.createTempFile("out_15N-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(
          getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,
          "--out_13C",tp,
          "--out_15N",tp2,
          "--derivatives",getClass.getResource("/MassLynx/correspondence_derivatives_empty.txt").getPath,
          "--metabolites",getClass.getResource("/MassLynx/Metabolites.dat").getPath))) match {
        case Success(_) => assert(Source.fromFile(tp).getLines().length == 1)
        case Failure(f) => f.printStackTrace();assert(false)
      }
    }

    test("MassLynx2IsocorCommand - with args derivatives bad definition. throw an error !") {
      val tp = File.createTempFile("out_13C-", ".tsv").getPath
      val tp2 = File.createTempFile("out_15N-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(
          getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,
          "--out_13C",tp,
          "--out_15N",tp2,
          "--derivatives",getClass.getResource("/MassLynx/correspondence_derivatives_baddef.txt").getPath,
          "--metabolites",getClass.getResource("/MassLynx/Metabolites.dat").getPath))) match {
        case Success(_) => assert(false)
        case Failure(_) => assert(true)
      }
    }

    test("MassLynx2IsocorCommand - with args derivatives") {
      val tp = File.createTempFile("out_13C-", ".tsv").getPath
      val tp2 = File.createTempFile("out_15N-", ".tsv").getPath

      Try(MassLynx2IsocorCommand.main(
        Array(
          getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath,
          "--out_13C",tp,
          "--out_15N",tp2,
          "--derivatives",getClass.getResource("/MassLynx/correspondence_derivatives.txt").getPath,
          "--metabolites",getClass.getResource("/MassLynx/Metabolites.dat").getPath))) match {
        case Success(_) => assert(Source.fromFile(tp).getLines().length > 1) // header and data
        case Failure(f) => f.printStackTrace();assert(false)
      }
    }
    test("MassLynx2IsocorCommand - with args metabolites.dat (formula)") {
      val toParse =
        """Compound 1:  His, M+7
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = QuantifyCompoundSummaryReportMassLynx(
        origin="",
        header=QuantifyCompoundSummaryReportMassLynx.Header(),
        results=QuantifyCompoundSummaryReportMassLynxParser.parseResults(toParse.split("\n").toList)
      )
      val list = MassLynxOutput2IsocorInput(Map("His"->"ACCQTAG"),formula=Map("His"->"C6H9N3O2")).transform(entry)

      assert(build_results_element(
        MassLynxOutput2IsocorInput(
          derivatives=Map("His"->"ACCQTAG"),
          formula=Map("His"->"C6H9N3O2")), list, 'C') == List())

      assert(build_results_element(
        MassLynxOutput2IsocorInput(
          derivatives=Map("His"->"ACCQTAG"),
          formula=Map("His"->"C7H9N3O2")), list, 'C') ==
        List(Isocor("GlyN15_A_3","His","ACCQTAG",7,96688,"2000")))
    }
  }
}
