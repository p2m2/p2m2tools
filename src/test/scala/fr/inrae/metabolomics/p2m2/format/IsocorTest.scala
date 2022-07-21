package fr.inrae.metabolomics.p2m2.format

import fr.inrae.metabolomics.p2m2.format.Isocor.{CompoundIsocor, HeaderField}
import utest.{TestSuite, Tests, test}

object IsocorTest extends TestSuite {
  val tests: Tests = Tests{
    test("toString") {
      assert(CompoundIsocor(
        Map(
          HeaderField.sample -> "test"
        )
      ).string(",") == "test,,,,,")
      assert(CompoundIsocor(
        Map(
          HeaderField.metabolite -> "test"
        )
      ).string(",") == ",test,,,,")
    }
  }
}
