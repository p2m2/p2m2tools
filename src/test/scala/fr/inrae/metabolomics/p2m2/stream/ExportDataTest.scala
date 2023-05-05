package fr.inrae.metabolomics.p2m2.stream

import fr.inrae.metabolomics.p2m2.format.conversions.FormatConversions
import fr.inrae.metabolomics.p2m2.format.ms.{GenericP2M2, Xcalibur}
import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur.CompoundSheetXcalibur
import fr.inrae.metabolomics.p2m2.parser.{GCMSParser, OpenLabCDSParser, QuantifySummaryReportMassLynxParser, XcaliburXlsParser}

import java.io.FileOutputStream
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import utest.{TestSuite, Tests, test}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}

object ExportDataTest extends TestSuite {

  def saveAsXls(filename : String, out : ByteArrayOutputStream): Unit = {

    val path = File.createTempFile(filename,".xls").getPath
    println(s" *** $path ***")
    val outputStream = new FileOutputStream(path)
    try {
      out.writeTo(outputStream)
    }
    finally if (outputStream != null) outputStream.close()
  }
  val tests: Tests = Tests {
    /*
    test("xlsP2M2 empty export") {
      val out : ByteArrayOutputStream = ExportData.xlsP2M2(GenericP2M2(Seq()))
      val in : ByteArrayInputStream = new ByteArrayInputStream(out.toByteArray)
      val workbook : HSSFWorkbook = new HSSFWorkbook(in)
//      saveAsXls("xlsP2M2",out)
      assert(workbook.getNumberOfSheets == 4)
      // + 2 ID , chromatographInjectionId
      assert(workbook.getSheetAt(0)
        .getRow(0).getLastCellNum == GenericP2M2.HeaderField.values.size)
    }
    test("xlsP2M2 basic export") {
      val out : ByteArrayOutputStream = ExportData.xlsP2M2(GenericP2M2(Seq(
        Map(
          GenericP2M2.HeaderField.sample -> "sample",
          GenericP2M2.HeaderField.metabolite -> "metabolite",
          GenericP2M2.HeaderField.retTime -> "0.1",
          GenericP2M2.HeaderField.height -> "0.101",
          GenericP2M2.HeaderField.area -> "198",
          GenericP2M2.HeaderField.vial -> "",
          GenericP2M2.HeaderField.acquisitionDate -> "2017-06-20 14:53:08.0000",
          GenericP2M2.HeaderField.exportDate -> "2017-06-21 14:53:08.0000",
          GenericP2M2.HeaderField.injectedVolume -> "0.1",
        ))))
      val in : ByteArrayInputStream = new ByteArrayInputStream(out.toByteArray)
      val workbook : HSSFWorkbook = new HSSFWorkbook(in)
      //saveAsXls("xlsP2M2",out)
      assert(workbook.getNumberOfSheets == 4)
      // + 2 ID , chromatographInjectionId
      assert(workbook.getSheetAt(0).getRow(0).getLastCellNum == GenericP2M2.HeaderField.values.size)
      assert(workbook.getSheetAt(0).getLastRowNum == 1)
      assert(workbook.getSheetAt(0).getRow(1).getLastCellNum == GenericP2M2FormatExtended.HeaderField.values.size)
      assert(workbook.getSheetAt(0).getRow(1).getCell(0).toString.nonEmpty)
      assert(workbook.getSheetAt(0).getRow(1).getCell(1).toString == "sample")
      assert(workbook.getSheetAt(0).getRow(1).getCell(2).toString == "metabolite")
      assert(workbook.getSheetAt(0).getRow(1).getCell(3).toString == "0.1")
      assert(workbook.getSheetAt(0).getRow(1).getCell(4).toString == "198")
      assert(workbook.getSheetAt(0).getRow(1).getCell(5).toString == "0.101")
      assert(workbook.getSheetAt(0).getRow(1).getCell(6).toString == "0.1")
      assert(workbook.getSheetAt(0).getRow(1).getCell(7).toString == "")
      assert(workbook.getSheetAt(0).getRow(1).getCell(8).toString == "2017-06-20 14:53:08.0000")
      assert(workbook.getSheetAt(0).getRow(1).getCell(9).toString == "2017-06-21 14:53:08.0000")
      assert(workbook.getSheetAt(0).getRow(1).getCell(10).toString.nonEmpty)

      /* samples : 1 */
      assert(workbook.getSheetAt(1).getRow(0).getLastCellNum == 1)
      /* metabolites : 1 */
      assert(workbook.getSheetAt(2).getRow(0).getLastCellNum == 1)
    }

    test("xlsP2M2 merge different format") {
      val mergeAllAcquisition : GenericP2M2 = Seq(
        GCMSParser.parse(getClass.getResource("/GCMS/13CPROT4.txt").getPath),
        OpenLabCDSParser.parse(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath),
        QuantifySummaryReportMassLynxParser.parse(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath),
        XcaliburXlsParser.parse(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath),
      ).foldLeft(GenericP2M2(Seq()))( (accumulator,v) => accumulator +v)

      val out : ByteArrayOutputStream = ExportData.xlsP2M2(mergeAllAcquisition)
      saveAsXls("xlsP2M2",out)
    }
*/
    test("xlsP2M2 create Xcalibur data.2") {
      val u1 = XcaliburXlsParser.parse(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      val u = XcaliburXlsParser.parse(getClass.getResource("/Xcalibur/data.test2.XLS").getPath)
/*
      u.results.foreach(
        s => s.compoundByInjection.foreach(
          d => println(d.get(Xcalibur.HeaderField.RT))
        )
      )
*/
      val r = FormatConversions.XcaliburToGenericP2M2(u)
/*
      r.samples.foreach(
        s => println(s.get(GenericP2M2.HeaderField.sample))
      )
*/

      val out: ByteArrayOutputStream = ExportData.xlsP2M2(r)
      saveAsXls("Xcalibur_data2", out)
    }
  }
}
