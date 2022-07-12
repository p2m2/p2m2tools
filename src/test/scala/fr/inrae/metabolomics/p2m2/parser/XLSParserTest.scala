package fr.inrae.metabolomics.p2m2.parser

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import utest.{TestSuite, Tests, test}

import java.io.{File, FileInputStream}

object XLSParserTest extends TestSuite {
  val fileTestPath: String = getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath

  val tests: Tests = Tests {

    test("getVerticalKeyValue") {
      val file = new FileInputStream(new File(fileTestPath))
      val workbook: HSSFWorkbook = new HSSFWorkbook(file)
      val res = XLSParserUtil.getVerticalKeyValue(workbook.getSheetAt(0))
      println(res.keys.mkString(","))
      assert(res("Component Name") == workbook.getSheetAt(0).getSheetName)
    }

    test("getHorizontalKeyValue") {
      val file = new FileInputStream(new File(fileTestPath))
      val workbook: HSSFWorkbook = new HSSFWorkbook(file)
      val res = XLSParserUtil.getHorizontalKeyValue(workbook.getSheetAt(0))
      assert(res("Filename") == "Sample Type")
    }

    test("getValuesRow") {
      val file = new FileInputStream(new File(fileTestPath))
      val workbook: HSSFWorkbook = new HSSFWorkbook(file)
      val res = XLSParserUtil.getValuesRow(workbook.getSheetAt(0),4,0)
      assert(res.head == "Filename")
    }

    test("getRowCellIndexesFromTerm") {
      val file = new FileInputStream(new File(fileTestPath))
      val workbook: HSSFWorkbook = new HSSFWorkbook(file)
      assert(XLSParserUtil.getRowCellIndexesFromTerm(workbook.getSheetAt(0),"Filename") == Seq(4->0))
    }
  }
}
