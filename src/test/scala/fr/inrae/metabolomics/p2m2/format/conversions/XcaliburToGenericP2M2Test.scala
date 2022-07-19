package fr.inrae.metabolomics.p2m2.format.conversions

import fr.inrae.metabolomics.p2m2.format.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.format.{GenericP2M2, Xcalibur}
import utest.{TestSuite, Tests, assert, test}

object XcaliburToGenericP2M2Test extends TestSuite {
  val tests: Tests = Tests{
    test("empty object to convert") {
      val o : GenericP2M2 =
        XcaliburToGenericP2M2.XcaliburToGenericP2M2(
          Xcalibur(
            origin = "none",
            results = Seq(CompoundSheetXcalibur())))

      assert(o.values.isEmpty)
    }
    test("basic partial object to convert") {
      val o : GenericP2M2 =
        XcaliburToGenericP2M2.XcaliburToGenericP2M2(
          Xcalibur(
            origin = "none",
            results = Seq(CompoundSheetXcalibur(
              compoundInformationHeader = Map(
                Xcalibur.HeaderSheetField.`Component Name` -> "testCompound"
              ),
              compoundByInjection = Seq(Map(
                Xcalibur.HeaderField.`Sample Name` -> "Name",
                Xcalibur.HeaderField.Area -> "10000",
              ))))))

      assert(o.values.nonEmpty)
      assert(o.values.head.get(GenericP2M2.HeaderField.metabolite).contains("testCompound"))
      assert(o.values.head.get(GenericP2M2.HeaderField.sample).contains("Name"))
      assert(o.values.head.get(GenericP2M2.HeaderField.area).contains("10000"))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.height))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.retTime))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.acquisitionDate))
      assert(!o.values.head.contains(GenericP2M2.HeaderField.injectedVolume))
    }
  }
}
