package fr.inrae.metabolomics.p2m2

import utest._

object GCMS2IsocorTest extends TestSuite{
  val tests = Tests{
    test("test1"){
      val paths = Array(
        getClass.getResource("/13CPROT1.txt").getPath,
        getClass.getResource("/13CPROT2.txt").getPath,
        getClass.getResource("/13CPROT3.txt").getPath,
        getClass.getResource("/13CPROT4.txt").getPath)

      GCMS2Isocor(paths)
    }
  }
}