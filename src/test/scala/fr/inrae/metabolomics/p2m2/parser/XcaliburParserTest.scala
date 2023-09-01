package fr.inrae.metabolomics.p2m2.parser

import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur.{HeaderField, HeaderSheetField}
import fr.inrae.metabolomics.p2m2.format.ms.Xcalibur
import fr.inrae.metabolomics.p2m2.parser.XLSParserTest.getClass
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
      assert(r.contains(HeaderSheetField.`Component Name`))
      assert(r.contains(HeaderSheetField.`Full Name`))
      assert(r.contains(HeaderSheetField.`User Name`))
      assert(r.contains(HeaderSheetField.`Origin Index`))
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
      assert(r.head.get(HeaderField.`Proc Method`)
        .contains("P:\\bia-prp_partage\\Projets_Interne_PRP\\PRP_Bia_partage\\DosagePolyphenols\\Reprocess_Methode\\050422_QC_Std290920"))
    }

    test("parse test xls file") {
      val out = XcaliburXlsParser.parseFile(getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      assert(out.origin == getClass.getResource("/Xcalibur/resuts_inj1_Long.XLS").getPath)
      assert(out.results.length==6)
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("CAT")))
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("CAT_MS")))
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("EC")))
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("EC_MS")))
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("PLZ")))
      assert(out.results.exists(_.compoundInformationHeader.get(HeaderSheetField.`Component Name`).contains("PLZ_MS")))
      assert(out.results(0).compoundByInjection(0).contains(Xcalibur.HeaderField.`Acq Date`))
      assert(out.results(0).compoundByInjection(0).get(Xcalibur.HeaderField.Duration).contains("70.0"))
      assert(out.results(0).compoundByInjection(0).get(Xcalibur.HeaderField.`Response Type`).contains("NA"))
      assert(out.results(0).compoundByInjection(0).get(Xcalibur.HeaderField.Area).contains("757255.68677067"))
      assert(out.results(0).compoundByInjection(0).get(Xcalibur.HeaderField.Height).contains("61223.3957790595"))
      assert(out.results(0).compoundInformationHeader.get(Xcalibur.HeaderSheetField.Date).contains("07/06/2022 08:57:12"))
    }


    test("data2.test") {
      val fileTestPath: String = getClass.getResource("/Xcalibur/data.test2.XLS").getPath
      val out = XcaliburXlsParser.parseFile(fileTestPath)
      assert(out.results(5).compoundByInjection(1).get(Xcalibur.HeaderField.RT).contains("NF"))
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
