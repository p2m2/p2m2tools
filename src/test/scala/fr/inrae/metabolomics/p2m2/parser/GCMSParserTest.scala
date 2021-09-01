package fr.inrae.metabolomics.p2m2.parser

import utest.{TestSuite, Tests, test}

object GCMSParserTest extends TestSuite{
  val tests = Tests{
    test("parse") {
      val toParse =
        """
          |[Header]
          |Data File Name	C:\Users\ydellero\Desktop\Projets CR\P2M2\TQD\210510_13C_Younes\13CPROT1.qgd
          |Output Date	23/08/2021
          |Output Time	14:08:03
          |""".stripMargin
      GCMSParser.get(toParse)
    }
  }
}
