package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputMassLynx.{CompoundField, Header}
import utest.{TestSuite, Tests, test}

object MassLynxParserTest extends TestSuite{
  val tests = Tests{
    test("file empty") {
      val toParse = ""
      assert( MassLynxParser.parseHeader(toParse.split("\n").toList) == Header() )
    }

    test("empty file - no date, no compound") {
      val toParse =
        """Quantify Compound Summary Report """.stripMargin
      assert( MassLynxParser.parseHeader(toParse.split("\n").toList) == Header() )
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

      assert( MassLynxParser.parseHeader(toParse.split("\n").toList) == h )
    }

    test("parse empty compound") {
      val toParse =
        """Compound 1:  NH4+""".stripMargin
      assert(MassLynxParser.parseResults(toParse.split("\n").toList) == List(("NH4+",List())))
    }

    test("parse compound") {
      val toParse =
        """Compound 1:  NH4+
          |
          |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
          |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      assert(MassLynxParser.parseResults(toParse.split("\n").toList) ==
        List(("NH4+",
          List(CompoundField("GlyN15_A_3",188,"","",1.78,96688,"","",796,"1:A,6",11.911,"17-sept-19",1151660)))))
    }

    test("complete file") {
      val toParse = """Quantify Compound Summary Report
                      |
                      |Printed Fri Sep 20 14:23:33 2019
                      |
                      |Compound 1:  NH4+
                      |
                      |	Name	Trace	Type	Std. Conc	RT	Area	uM	%Dev	S/N	Vial	Height/Area	Acq.Date	Height
                      |1	GlyN15_A_3	188			1.78	96688			796	1:A,6	11.911	17-sept-19	1151660""".stripMargin

      val results : OutputMassLynx = MassLynxParser.get("test",toParse.split("\n").toList)

      assert( results.header == Header(Some("Fri Sep 20 14:23:33 2019")) )
      assert( results.results ==
        List(("NH4+",
          List(CompoundField("GlyN15_A_3",188,"","",1.78,96688,"","",796,"1:A,6",11.911,"17-sept-19",1151660)))))
    }

  }


}