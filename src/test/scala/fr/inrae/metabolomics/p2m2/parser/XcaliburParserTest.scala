package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.tools.format.Xcalibur.{HeaderField, HeaderSheetField}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import utest.{TestSuite, Tests, test}

import java.io.{File, FileInputStream}

object XcaliburParserTest extends TestSuite {

  val tests: Tests = Tests {
    val file = new FileInputStream(new File(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
    val workbook: HSSFWorkbook = new HSSFWorkbook(file)
    val res = XLSParserUtil.getVerticalKeyValue(workbook.getSheetAt(0))

    test("getHeaderSheet") {
      val r = XcaliburXlsParser.getHeaderSheet(res)
      assert(r.contains(HeaderSheetField.Component_Name))
      assert(r.contains(HeaderSheetField.Full_Name))
      assert(r.contains(HeaderSheetField.User_Name))
      assert(r.contains(HeaderSheetField.Origin_Index))
      assert(r.contains(HeaderSheetField.Equation))
    }

    test("getResults of empty sheet") {
      val workbook = new HSSFWorkbook()
      val sheet = workbook.createSheet("FirstSheet")
      val res = XcaliburXlsParser.getResults(sheet)
      assert(res.isEmpty)
    }

    test("getHeaderSheet") {
      val r = XcaliburXlsParser.getResults(workbook.getSheetAt(0))
      assert(r.length == 1)
      assert(r.head.get(HeaderField.Filename).contains("020622_Std_290920_106_inj1_220603204908"))
      assert(r.head.get(HeaderField.Proc_Method)
        .contains("P:\\bia-prp_partage\\Projets_Interne_PRP\\PRP_Bia_partage\\DosagePolyphenols\\Reprocess_Methode\\050422_QC_Std290920"))
    }

    test("parse test xls file") {
      val out = XcaliburXlsParser.parse(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      assert(out.origin == getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      assert(out.injections.length==6)
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("CAT")))
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("CAT_MS")))
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("EC")))
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("EC_MS")))
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("PLZ")))
      assert(out.injections.exists(_.compoundInformationHeader.get(HeaderSheetField.Component_Name).contains("PLZ_MS")))
    }

    test("extensionIsCompatible") {
      assert(XcaliburXlsParser.extensionIsCompatible("test.XLS"))
      assert(XcaliburXlsParser.extensionIsCompatible("test.xls"))
      assert(!XcaliburXlsParser.extensionIsCompatible("test.XL"))
      assert(!XcaliburXlsParser.extensionIsCompatible("test"))
      assert(!XcaliburXlsParser.extensionIsCompatible(""))
    }

    test("sniffFile") {
      assert(!XcaliburXlsParser.sniffFile("test.XLS"))
      assert(XcaliburXlsParser.sniffFile(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/MassLynx/targeted/190522_97.txt").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/Xcalibur/bad_file_xls.xls").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/GCMS/13CPROT4.txt").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/MassLynx/mass_15Ngly.txt").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/OpenLabCDS/Report_Ex1.txt").getPath))
      assert(!XcaliburXlsParser.sniffFile(getClass.getResource("/MassLynx/xml/quandata.xml").getPath))
    }

  }
}
