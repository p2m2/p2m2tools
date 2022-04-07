package fr.inrae.metabolomics.p2m2.converter

import fr.inrae.metabolomics.p2m2.parser.MassLynxParser
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
      assert( MassLynxOutput2IsocorInput(Map()).transform(entry) == List())
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

      assert( MassLynxOutput2IsocorInput(Map()).transform(entry) == List())
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

      assert( MassLynxOutput2IsocorInput(Map()).transform(entry) == List())
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

      assert( MassLynxOutput2IsocorInput(Map(),listSampleToRemove=Seq("M+H")).transform(entry) == List())
    }

    test("basic run with a sample M+H") {
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
      assert( MassLynxOutput2IsocorInput(Map("M+H"->"ACCQTAG")).transform(entry) == List("GlyN15_A_3\tM+H\tACCQTAG\t0\t96688\t2000"))
    }

    test("basic run with a sample M+H, resolution=1000") {
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
      assert( MassLynxOutput2IsocorInput(Map("M+H"->"ACCQTAG"),resolution=1000).transform(entry) ==
        List("GlyN15_A_3\tM+H\tACCQTAG\t0\t96688\t1000"))
    }
    test("basic run with a sample M+H, resolution=1000") {
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

      assert( MassLynxOutput2IsocorInput(Map(),resolution=1000).transform(entry) == List())
    }

    test("basic run with a sample M+H, resolution=1000, map( GlyN15_A_3 => 'TOTO')") {
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

      assert( MassLynxOutput2IsocorInput(Map("M+H" -> "TOTO"),resolution=1000).transform(entry) ==
        List("GlyN15_A_3\tM+H\tTOTO\t0\t96688\t1000"))
    }

    test("basic run with a sample M+H, resolution=1000, map( GlyN15_A_3 => 'TOTO')") {
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

      assert( MassLynxOutput2IsocorInput(Map("M+H" -> "TOTO"),resolution=1000).transform(entry) ==
        List("GlyN15_A_3\tM+H\tTOTO\t0\t96688\t1000"))
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

      assert( MassLynxOutput2IsocorInput(Map("TATA" -> "TOTO"),resolution=1000).transform(entry) == List())
    }
  }
}
