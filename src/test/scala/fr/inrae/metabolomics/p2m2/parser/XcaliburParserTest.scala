package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.parser.GCMSParserTest.getClass
import fr.inrae.metabolomics.p2m2.parser.XLSParserTest.fileTestPath
import fr.inrae.metabolomics.p2m2.tools.format.output.OutputXcalibur.{HeaderField, HeaderSheetField}
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

    test("getHeaderSheet") {
      val r = XcaliburXlsParser.getResults(workbook.getSheetAt(0))
      assert(r.length == 1)
      assert(r.head.get(HeaderField.Filename).contains("020622_Std_290920_106_inj1_220603204908"))
      assert(r.head.get(HeaderField.Proc_Method)
        .contains("P:\\bia-prp_partage\\Projets_Interne_PRP\\PRP_Bia_partage\\DosagePolyphenols\\Reprocess_Methode\\050422_QC_Std290920"))
    }
  }
}
