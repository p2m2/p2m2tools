package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.MassLynxParser
import fr.inrae.metabolomics.p2m2.tools.format.input.InputIsocor
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx
import utest.{TestSuite, Tests, test}

object  MassLynxOutput2IsocorInputTest extends TestSuite {
  val tests = Tests {
    test("nothing") {
      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=List()
      )
      assert( MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map()).transform(entry) == List())
    }
    test("basic run with a sample to remove NH4") {
      val toParse =
        """Compound 1:  NH4+
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map()).transform(entry) == List())
    }

    test("basic run with unknown sample TOTO") {
      val toParse =
        """Compound 1:  TOTO
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map()).transform(entry) == List())
    }

    test("basic run with a sample M+H inializing listSampleToRemove='M+H'") {
      val toParse =
        """Compound 1:  M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map(),listSampleToRemove=Seq("M+H")).transform(entry) == List())
    }

    test("basic run with a sample His") {
      val toParse =
        """Compound 1:  His, M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert(MassLynxOutput2IsocorInput(derivatives=Map("His"->"ACCQTAG"),formula=Map())
        .transform(entry) == List(InputIsocor("GlyN15_A_3","His","ACCQTAG",0,96688,"2000")))
    }

    test("basic run with a sample His, resolution=1000") {
      val toParse =
        """Compound 1:  His, M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )
      assert( MassLynxOutput2IsocorInput(Map("His"->"ACCQTAG"),formula=Map(),resolution=1000).transform(entry) ==
        List(InputIsocor("GlyN15_A_3","His","ACCQTAG",0,96688,"1000")))
    }
    test("basic run with a sample His, resolution=1000") {
      val toParse =
        """Compound 1:  His, M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map(),resolution=1000).transform(entry) == List())
    }

    test("basic run with a sample His, resolution=1000, map( GlyN15_A_3 => 'TOTO')") {
      val toParse =
        """Compound 1:  His, M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map("His" -> "TOTO"),formula=Map(),resolution=1000).transform(entry) ==
        List(InputIsocor("GlyN15_A_3","His","TOTO",0,96688,"1000")))
    }

    test("basic run with a sample M+H, resolution=1000, map( GlyN15_A_3 => 'TOTO')") {
      val toParse =
        """Compound 1:  His, M+H
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )

      assert( MassLynxOutput2IsocorInput(derivatives=Map("His" -> "TOTO"),formula=Map(),resolution=1000).transform(entry) ==
        List(InputIsocor("GlyN15_A_3","His","TOTO",0,96688,"1000")))
    }

    test("basic run with a sample M+H, resolution=1000, map( GlyN15_A_3 => 'TOTO')") {
      val toParse =
        """Compound 1:  TATA
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val entry = OutputMassLynx(
        origin="",
        header=OutputMassLynx.Header(),
        results=MassLynxParser.parseResults(toParse.split("\n").toList)
      )
      assert( MassLynxOutput2IsocorInput(derivatives=Map("TATA" -> "TOTO"),formula=Map(),resolution=1000).transform(entry) == List())
    }

    test("getNumberElementFromFormula - None definition get 0") {
      assert(MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map()).getNumberElementFromFormula("some",'C') == 0)
    }

    test("getNumberElementFromFormula - CHN definition get 1 for Carbone element") {
      assert(
        MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map("some" -> "CHN"))
          .getNumberElementFromFormula("some",'C') == 1)
    }

    test("getNumberElementFromFormula - C2HN definition get 2 for Carbone element") {
      assert(
        MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map("some" -> "C2HN"))
          .getNumberElementFromFormula("some",'C') == 2)
    }
    test("getNumberElementFromFormula - C16HN definition get 16 for Carbone element") {
      assert(
        MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map("some" -> "C16HN"))
          .getNumberElementFromFormula("some",'C') == 16)
    }

    test("getNumberElementFromFormula - C2H5N10 definition get 16 for Carbone element") {
      val t = MassLynxOutput2IsocorInput(derivatives=Map(),formula=Map("some" -> "C2H5N10"))
      assert(t.getNumberElementFromFormula("some",'C') == 2)
      assert(t.getNumberElementFromFormula("some",'H') == 5)
      assert(t.getNumberElementFromFormula("some",'N') == 10)
      assert(t.getNumberElementFromFormula("some",'U') == 0)
    }
  }
}
