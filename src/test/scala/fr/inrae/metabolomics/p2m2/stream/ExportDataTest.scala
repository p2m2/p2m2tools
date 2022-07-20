package fr.inrae.metabolomics.p2m2.stream

import fr.inrae.metabolomics.p2m2.format.GenericP2M2
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import utest.{TestSuite, Tests, test}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}

object ExportDataTest extends TestSuite {

  def saveAsXls(filename : String, out : ByteArrayOutputStream): Unit = {
    import java.io.FileOutputStream
    val path = File.createTempFile(filename,".xls").getPath
    println(s" *** $path ***")
    val outputStream = new FileOutputStream(path)
    try {
      out.writeTo(outputStream)
    }
    finally if (outputStream != null) outputStream.close()
  }
  val tests: Tests = Tests {
    test("xlsP2M2 empty export") {
      val out : ByteArrayOutputStream = ExportData.xlsP2M2(GenericP2M2(Seq()))
      val in : ByteArrayInputStream = new ByteArrayInputStream(out.toByteArray)
      val workbook : HSSFWorkbook = new HSSFWorkbook(in)
//      saveAsXls("xlsP2M2",out)
      assert(workbook.getNumberOfSheets == 3)
      assert(workbook.getSheetAt(0).getRow(0).getLastCellNum == GenericP2M2.HeaderField.values.size)
    }
    test("xlsP2M2 basic export") {
      val out : ByteArrayOutputStream = ExportData.xlsP2M2(GenericP2M2(Seq(
        Map(
          GenericP2M2.HeaderField.sample -> "sample",
          GenericP2M2.HeaderField.metabolite -> "metabolite",
          GenericP2M2.HeaderField.retTime -> "0.1",
          GenericP2M2.HeaderField.height -> "0.101",
          GenericP2M2.HeaderField.area -> "198",
          GenericP2M2.HeaderField.acquisitionDate -> "12/12/2022",
          GenericP2M2.HeaderField.injectedVolume -> "0.1",
        ))))
      val in : ByteArrayInputStream = new ByteArrayInputStream(out.toByteArray)
      val workbook : HSSFWorkbook = new HSSFWorkbook(in)
      //saveAsXls("xlsP2M2",out)
      assert(workbook.getNumberOfSheets == 3)
      assert(workbook.getSheetAt(0).getRow(0).getLastCellNum == GenericP2M2.HeaderField.values.size)
      assert(workbook.getSheetAt(0).getLastRowNum == 1)
      assert(workbook.getSheetAt(0).getRow(1).getLastCellNum == GenericP2M2.HeaderField.values.size)
      assert(workbook.getSheetAt(0).getRow(1).getCell(0).toString == "sample")
      assert(workbook.getSheetAt(0).getRow(1).getCell(1).toString == "metabolite")
      assert(workbook.getSheetAt(0).getRow(1).getCell(2).toString == "0.1")
      assert(workbook.getSheetAt(0).getRow(1).getCell(3).toString == "198")
      assert(workbook.getSheetAt(0).getRow(1).getCell(4).toString == "0.101")
      assert(workbook.getSheetAt(0).getRow(1).getCell(5).toString == "0.1")
      assert(workbook.getSheetAt(0).getRow(1).getCell(6).toString == "12/12/2022")
    }

  }
}
